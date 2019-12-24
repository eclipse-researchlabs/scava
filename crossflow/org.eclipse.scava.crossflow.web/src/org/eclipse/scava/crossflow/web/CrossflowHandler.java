package org.eclipse.scava.crossflow.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.eclipse.scava.crossflow.runtime.Cache;
import org.eclipse.scava.crossflow.runtime.DirectoryCache;
import org.eclipse.scava.crossflow.runtime.Mode;
import org.eclipse.scava.crossflow.runtime.Workflow;
import org.eclipse.scava.crossflow.runtime.utils.CrossflowLogger;
import org.eclipse.scava.crossflow.runtime.utils.LogLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CrossflowHandler implements Crossflow.Iface {

	protected BrokerService brokerService;
	protected CrossflowServlet servlet;

	public CrossflowHandler(CrossflowServlet servlet) {
		this.servlet = servlet;
	}

	protected ClassLoader getClassLoader() throws Exception {
		return Thread.currentThread().getContextClassLoader();
	}

	@SuppressWarnings("resource")
	@Override
	public boolean startExperiment(String experimentId, boolean worker) throws TException {
		Experiment experiment = getExperiment(experimentId);
		ExperimentRegistry.addExperiment(experiment);
		if (!isBrokerRunning()) {
			startBroker();
		}
		Mode mode = Mode.MASTER;
		if (!worker)
			mode = Mode.MASTER_BARE;

		URLClassLoader classLoader;
		try {
			classLoader = new URLClassLoader(
					new URL[] { new File(servlet.getServletContext()
							.getRealPath("experiments/" + experimentId + "/" + experiment.getJar())).toURI().toURL() },
					Thread.currentThread().getContextClassLoader());
		} catch (MalformedURLException e) {
			throw new TApplicationException(TApplicationException.INTERNAL_ERROR,
					"Unable to load workflow jar to class path");
		}

		Workflow workflow;
		try {
			workflow = (Workflow) classLoader.loadClass(experiment.getClassName()).getConstructor(Mode.class)
					.newInstance(mode);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			throw new TApplicationException(TApplicationException.INTERNAL_ERROR,
					"Unable to create instance of workflow main class.");
		} finally {
			// FIXME We should close the class loader somewhere!
			// classLoader.close();
		}
		if (workflow != null) {
			workflow.getSerializer().setClassloader(classLoader);
			workflow.setInstanceId(experimentId);
			workflow.createBroker(false);
			File cacheDir = new File(servlet.getServletContext().getRealPath("experiments/" + experimentId + "/cache"));
			cacheDir.mkdirs();
			workflow.setCache(new DirectoryCache(cacheDir));
			workflow.setInputDirectory(new File(servlet.getServletContext()
					.getRealPath("experiments/" + experimentId + "/" + experiment.getInputDirectory())));
			workflow.setOutputDirectory(new File(servlet.getServletContext()
					.getRealPath("experiments/" + experimentId + "/" + experiment.getOutputDirectory())));

			try {
				workflow.run();
			} catch (Exception e) {
				workflow.log(LogLevel.ERROR,
						"Workflow " + workflow.getName() + " throwed an exception. " + e.getMessage());
				throw new TApplicationException(TApplicationException.INTERNAL_ERROR, "Error executiong the workflow");
			}
			// add new workflow to registry
			ExperimentRegistry.addWorkflow(workflow, experimentId);
			// remove workflow from registry after termination
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						workflow.awaitTermination();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
					ExperimentRegistry.removeWorkflow(experimentId);
				}
			}).start();
			return true;
		}
		return false;
	}

	@Override
	public boolean stopExperiment(String experimentId) throws TException {
		Workflow workflow = ExperimentRegistry.getWorkflow(experimentId);
		boolean result = (workflow != null);
		if (result) {
			workflow.log(LogLevel.INFO, "Workflow " + workflow.getName() + " termination requested.");
			workflow.terminate();
			result = true;
			System.out.println("terminated workflow: " + experimentId + "");
		} else
			System.out.println("warning, trying to terminate workflow: " + experimentId
					+ ", but could not find it in the registry.");
		return result;
	}

	@Override
	public boolean isExperimentRunning(String experimentId) throws TException {
		Workflow workflow = ExperimentRegistry.getWorkflow(experimentId);
		if (workflow != null) {
			return workflow.hasTerminated();
		} else {
			return false;
		}
	}

	@Override
	public Experiment getExperiment(String experimentId) throws TException {
		Experiment experiment = null;
		try {
			experiment = getExperiments().stream().filter(e -> e.getId().equals(experimentId))
					.collect(Collectors.toList()).iterator().next();
		} catch (Exception e) {
			System.err.println("Experiment with id '" + experimentId + "' does not exist.");
			return experiment;
		}
		return experiment;
	}

	@Override
	public List<Experiment> getExperiments() throws TException {
		File experimentsDirectory = new File(servlet.getServletContext().getRealPath("experiments"));
		if (experimentsDirectory.exists()) {
			List<Experiment> experiments = new ArrayList<>();
			for (File experimentDirectory : experimentsDirectory.listFiles()) {
				if (experimentDirectory.isDirectory()) {
					experiments.add(getExperiment(experimentDirectory));
				}
			}
			return experiments;
		}
		return Collections.emptyList();
	}

	@Override
	public boolean resetExperiment(String experimentId) throws TException {

		Experiment experiment = getExperiment(experimentId);
		System.out.println(experiment.getOutputDirectory() == null);

		if (experiment.getOutputDirectory() != null) {
			File output = new File(servlet.getServletContext()
					.getRealPath("experiments/" + experimentId + "/" + experiment.getOutputDirectory()));
			if (output != null && output.exists()) {
				delete(output);
			}
		}
		File cache = new File(servlet.getServletContext().getRealPath("experiments/" + experimentId + "/cache"));
		if (cache != null && cache.exists()) {
			delete(cache);
		}
		return true;

	}

	@Override
	public boolean clearQueueCache(String experimentId, String queueName) throws TException {
		Workflow w = ExperimentRegistry.getWorkflow(experimentId);
		if (w != null) {
			System.err.println("Failed to clear queue. Unable to clear queue from running workflow with experimentId \'"
					+ experimentId + "\'. Has this experiment been stopped yet?");
			return false;
		} else {
			Cache cache = ExperimentRegistry.getCache(experimentId);
			if (cache != null) {
				cache.clear(queueName);
			}
			return true;
		}
	}// clearQueueCache

	public void delete(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				delete(child);
			}
		}
		file.delete();
	}

	@Override
	public Table getContent(FileDescriptor fileDescriptor) throws TException {

		File file = new File(servlet.getServletContext()
				.getRealPath("experiments/" + fileDescriptor.getExperimentId() + "/" + fileDescriptor.getPath()));
		if (!file.exists())
			return new Table();

		try {
			Table table = new Table();
			boolean header = true;
			FileReader fileReader = new FileReader(file);
			for (CSVRecord record : CSVFormat.RFC4180.parse(fileReader)) {
				Row row = new Row();
				for (int i = 0; i < record.size(); i++) {
					row.addToCells(record.get(i));
				}
				if (header) {
					table.setHeader(row);
					header = false;
				} else {
					table.addToRows(row);
				}
			}
			fileReader.close();
			return table;
		} catch (Exception ex) {
			throw new TException(ex);
		}

	}

	protected Experiment getExperiment(File experimentDirectory) throws TException {
		try {
			File config = new File(experimentDirectory, "experiment.xml");
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new FileInputStream(config));
			Element experimentElement = document.getDocumentElement();
			Experiment experiment = new Experiment();
			experiment.setId(experimentDirectory.getName());
			experiment.setTitle(experimentElement.getAttribute("title"));
			experiment.setClassName(experimentElement.getAttribute("class"));
			experiment.setJar(experimentElement.getAttribute("jar"));
			experiment.setSummary(experimentElement.getAttribute("summary"));
			experiment.setInputDirectory(experimentElement.getAttribute("input"));
			experiment.setOutputDirectory(experimentElement.getAttribute("output"));
			experiment.setCached(new File(experimentDirectory, "cache").exists());
			if (experiment.getOutputDirectory() != null) {
				experiment.setExecuted(new File(experimentDirectory, experiment.getOutputDirectory()).exists());
			}

			for (Element descriptionElement : toElementList(experimentElement.getElementsByTagName("description"))) {
				experiment.setDescription(descriptionElement.getTextContent());
			}

			for (Element inputElement : toElementList(experimentElement.getElementsByTagName("input"))) {
				FileDescriptor fileDescriptor = elementToFileDescriptor(inputElement);
				fileDescriptor.setExperimentId(experiment.getId());
				fileDescriptor.setInput(true);
				experiment.addToFileDescriptors(fileDescriptor);
			}

			for (Element outputElement : toElementList(experimentElement.getElementsByTagName("output"))) {
				FileDescriptor fileDescriptor = elementToFileDescriptor(outputElement);
				fileDescriptor.setExperimentId(experiment.getId());
				fileDescriptor.setInput(false);
				experiment.addToFileDescriptors(fileDescriptor);
			}

			Workflow workflow = ExperimentRegistry.getWorkflow(experiment.getId());
			if (workflow != null && !workflow.hasTerminated()) {
				experiment.status = "running";
			} else {
				experiment.status = "stopped";
			}

			return experiment;
		} catch (Exception ex) {
			throw new TException(ex);
		}
	}

	@Override
	public void startBroker() throws TException {

		if (isBrokerRunning())
			return;

		try {
			brokerService = new BrokerService();
			brokerService.setUseJmx(true);
			brokerService.addConnector("tcp://localhost:61616");
			brokerService.addConnector("stomp://localhost:61613");
			brokerService.addConnector("ws://localhost:61614");
			brokerService.start();
		} catch (Exception ex) {
			throw new TException(ex);
		}
	}

	@Override
	public void stopBroker() throws TException {
		if (brokerService != null) {
			try {
				brokerService.deleteAllMessages();
				brokerService.stopGracefully("", "", 1000, 1000);
			} catch (Exception ex) {
				throw new TException(ex);
			}
			brokerService = null;
		}
	}

	@Override
	public boolean isBrokerRunning() throws TException {
		if (!available("localhost", 61616)) {
			return true; // port already used, broker is already running
		}
		// port not used, broker not running
		return false;
	}

	private static boolean available(String host, int port) {
		try (Socket ignored = new Socket(host, port)) {
			return false;
		} catch (IOException ignored) {
			return true;
		}
	}

	@Override
	public Diagnostics getDiagnostics() throws TException {
		Diagnostics diagnostics = new Diagnostics();
		diagnostics.setBrokerRunning(isBrokerRunning());
		diagnostics.setRootDirectory(servlet.getServletContext().getRealPath(""));
		return diagnostics;
	}

	protected ArrayList<Element> toElementList(NodeList nodeList) {
		ArrayList<Element> list = new ArrayList<Element>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			list.add((Element) nodeList.item(i));
		}
		return list;
	}

	protected FileDescriptor elementToFileDescriptor(Element element) {
		FileDescriptor fileDescriptor = new FileDescriptor();
		fileDescriptor.setTitle(element.getAttribute("title"));
		Element parentElem = (Element) element.getParentNode();
		if (parentElem.getAttribute(element.getNodeName()) != null) {
			fileDescriptor.setPath(
					Paths.get(parentElem.getAttribute(element.getNodeName()), element.getAttribute("path")).toString());
		} else {
			fileDescriptor.setPath(element.getAttribute("path"));
		}
		return fileDescriptor;
	}
}
