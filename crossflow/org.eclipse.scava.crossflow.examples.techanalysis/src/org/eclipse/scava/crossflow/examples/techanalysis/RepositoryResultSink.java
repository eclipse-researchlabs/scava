/*******************************************************************************
 * Copyright (c) 2019 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributor(s):
 *      Patrick Neubauer - initial API and implementation
 ******************************************************************************/
package org.eclipse.scava.crossflow.examples.techanalysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.scava.crossflow.runtime.utils.CsvWriter;
import org.eclipse.scava.crossflow.runtime.utils.LogLevel;

public class RepositoryResultSink extends RepositoryResultSinkBase {

	protected HashMap<String, AnalysisResult> results = new HashMap<String, AnalysisResult>();

	private boolean started = false;
	private Timer t = new Timer();

	@Override
	public void consumeRepositoryResults(AnalysisResult analysisResult) throws Exception {
		// System.out.println("sink consuming: " + analysisResult);
		// System.out.println(analysisResult.repository.name);
		if (analysisResult.technology.fileExt.startsWith("//"))
			return;

		if (!started)
			t.schedule(new TimerTask() {
				@Override
				public void run() {
					flushToDisk(false);
				}
			}, 3000, 3000);
		started = true;

		String key = analysisResult.repository.url + "::" + analysisResult.technology.techKey + "::"
				+ analysisResult.technology.fileExt;

		// wait if the current data is being written to disk
		if (flushing) {
			Thread.sleep(100);
		}

		if (!results.containsKey(key) && analysisResult.repository.getName() != null) {
			// add new item if it contains files of the relevant technology
			if (analysisResult.getFileCount() > 0)
				results.put(key, analysisResult);

		} else if (results.containsKey(key)) {
			// supplement new item with existing information (if available)
			if (analysisResult.getFileCount() > 0) {

				AnalysisResult existingResult = results.get(key);

				if (analysisResult.getAuthorCount() < existingResult.getAuthorCount()) {
					System.out.println("RepositoryResultSink: replacing authors: " + analysisResult.getAuthorCount()
							+ "" + existingResult.getAuthorCount());
					analysisResult.setAuthorCount(existingResult.getAuthorCount());
				}
				if (analysisResult.getFileCount() < existingResult.getFileCount()) {
					System.out.println("RepositoryResultSink: replacing files: " + analysisResult.getFileCount() + ""
							+ existingResult.getFileCount());
					analysisResult.setFileCount(existingResult.getFileCount());
				}
				results.put(key, analysisResult);
			}
		}
	}

	@Override
	public void close() {
		System.out.println("RepositoryResultSink close() called.");
		t.cancel();
		// flushToDisk(true);
		while (flushing)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	boolean flushing = false;

	private synchronized void flushToDisk(boolean force) {
		CsvWriter writer = null;
		try {
			if (!flushing || force) {
				flushing = true;
				File output = new File(workflow.getOutputDirectory(), "output.csv");
				File outputTemp = new File(workflow.getOutputDirectory(), "output-temp.csv");

				writer = new CsvWriter(outputTemp.getAbsolutePath(), "Technology", "repo count", "file count",
						"unique author count");

				HashMap<String, AnalysisResult> merged = new HashMap<String, AnalysisResult>();

				for (AnalysisResult result : results.values()) {
					// if (result.technology.techKey.equals("texttransformation")) {
					String key = result.technology.techKey + "::" + result.technology.fileExt;
					if (merged.containsKey(key)) {
						AnalysisResult newValues = merged.get(key);
						// proxy for number of repos
						newValues.setFailures(newValues.getFailures() + 1);
						newValues.setFileCount(newValues.getFileCount() + result.getFileCount());
						newValues.setAuthorCount(newValues.getAuthorCount() + result.getAuthorCount());
						merged.put(key, newValues);
					} else {
						AnalysisResult clone = new AnalysisResult();
						clone.setFailures(1);
						clone.setFileCount(result.getFileCount());
						clone.setAuthorCount(result.getAuthorCount());
						clone.setRepository(result.getRepository());
						clone.setTechnology(result.getTechnology());
						merged.put(key, clone);
					}
					// }
				}
				for (AnalysisResult result : merged.values())
					if (!result.technology.fileExt.startsWith("//"))
						writer.writeRecord(result.technology.techKey + "::" + result.technology.fileExt,
								result.getFailures(), result.getFileCount(), result.getAuthorCount());

				writer.flush();
				writer.close();

				boolean removedPreviousOut = false;
				if (output.exists())
					removedPreviousOut = output.delete();
				// outputTemp.renameTo(new File(workflow.getOutputDirectory(), "output.csv"));
				if (removedPreviousOut) {
					try {
						Files.move(Paths.get(outputTemp.getPath()),
								Paths.get(new File(workflow.getOutputDirectory(), "output.csv").getPath()),
								StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
					} catch (Exception e) {
						System.out.println("WARNING: RepositoryResultSink failed to move temp output file.");
					}
				} else
					System.out.println("INFO: RepositoryResultSink failed to remove old output file.");
			}
		} catch (Exception ex) {

			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					//
				}

			// ex.printStackTrace();

			log(LogLevel.ERROR, "Exception occurred while flushing workflow output to disk. Type: "
					+ ex.getClass().getName() + " Message: " + ex.getMessage());
		} finally {
			flushing = false;
		}
	}
	// ----

}
