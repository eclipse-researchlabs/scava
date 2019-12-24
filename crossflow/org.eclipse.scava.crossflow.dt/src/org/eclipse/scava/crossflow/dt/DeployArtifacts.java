package org.eclipse.scava.crossflow.dt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

public class DeployArtifacts implements IObjectActionDelegate {

	protected Shell shell;
	protected ISelection selection;
	protected final String webAppProjectName = "org.eclipse.scava.crossflow.web";
	protected String experimentName;
	protected String serverIp;

	protected boolean startImmediately = false;

	@Override
	public void run(IAction action) {
		IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		File projectFolder = selectedFile.getProject().getLocation().toFile();
		String projectFolderLocation = projectFolder.getPath();
		try {

			// find experiment folder
			File experimentFolder = new File(projectFolder, "artifacts");
			if (!experimentFolder.exists())
				return;

			Collection<String> bannedNames = new LinkedList<String>();
			bannedNames.add("in.zip");
			File zipfile = new File(experimentFolder, getFileWithExt(experimentFolder, ".zip", bannedNames));
			if (zipfile == null || !zipfile.exists())
				return;
			System.out.println(zipfile);

			File config = new File(experimentFolder,
					getFileWithExt(experimentFolder, "experiment.xml", new LinkedList<String>()));
			if (config == null || !config.exists())
				return;
			System.out.println(config);

			serverIp = getWebServerIp(config);
			experimentName = getExperimentName(config);
			System.out.println(serverIp);
			// send zip to webserver
			//
			uploadExperiment(zipfile);

		} catch (Exception e) {
			ExceptionHandler.handle(e, shell);
		}
	}

	protected void uploadExperiment(File zipfile) throws Exception {

		HttpClient httpclient = new DefaultHttpClient();

		String address = "http://" + serverIp + "/" + webAppProjectName + "/uploadExperiment";
		
		System.out.println("uploading workflow to: " + address);
		System.out.println("zip file located at: " + zipfile);
		
		HttpPost httppost = new HttpPost(address);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addPart("experimentZip", new FileBody(zipfile));
		builder.addPart("inputName", new StringBody(experimentName));

		if (startImmediately == true)
			builder.addPart("inputCheck", new StringBody("on"));

		HttpEntity entity = builder.build();

		httppost.setEntity(entity);

		HttpResponse response = httpclient.execute(httppost);

		System.out.println(response.toString());

		if (startImmediately == true) {

			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL("http://" + serverIp
					+ "/" + webAppProjectName + "/index.jsp?id=" + experimentName + "&launchExperiment=true"));

		}

	}

	private String getWebServerIp(File config) throws Exception {
		BufferedReader r = new BufferedReader(new FileReader(config));
		String webserverprefix = "webServer=\"";
		String next;
		String ret = null;
		while ((next = r.readLine()) != null)
			if (next.trim().startsWith(webserverprefix))
				ret = next.trim().substring(next.trim().indexOf(webserverprefix) + webserverprefix.length(),
						next.trim().length() - 1);
		r.close();
		return ret;
	}

	private String getExperimentName(File config) throws Exception {
		BufferedReader r = new BufferedReader(new FileReader(config));
		String webserverprefix = "title=\"";
		String next;
		String ret = null;
		while ((next = r.readLine()) != null)
			if (next.trim().startsWith(webserverprefix))
				ret = next.trim().substring(next.trim().indexOf(webserverprefix) + webserverprefix.length(),
						next.trim().length() - 1);
		r.close();
		return ret;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	protected String getFileWithExt(File directory, String ext, Collection<String> banned) {
		for (String f : directory.list()) {
			if (f.endsWith(ext))
				if (!banned.contains(f))
					return f;
		}
		return null;
	}

}
