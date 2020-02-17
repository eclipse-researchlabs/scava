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
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
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
			}, 2000, 2000);
		started = true;

		if (!results.containsKey(analysisResult.repository.getName()) && analysisResult.repository.getName() != null) {
			// add new item
			results.put(analysisResult.repository.getName(), analysisResult);

		} else if (results.containsKey(analysisResult.repository.getName())) {
			// supplement new item with existing information (if available)
			AnalysisResult existingResult = results.get(analysisResult.repository.getName());

			if (analysisResult.getAuthorCount() < existingResult.getAuthorCount()) {
				analysisResult.setAuthorCount(existingResult.getAuthorCount());
			}
			if (analysisResult.getFileCount() < existingResult.getFileCount()) {
				analysisResult.setFileCount(existingResult.getFileCount());
			}
			results.replace(existingResult.repository.getName(), analysisResult);
		}

	}

	@Override
	public void close() {
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

	HashMap<String, AnalysisResult> merged = new HashMap<String, AnalysisResult>();

	private synchronized void flushToDisk(boolean force) {
		try {
			if (!flushing || force) {
				flushing = true;
				File output = new File(workflow.getOutputDirectory(), "output.csv");
				File outputTemp = new File(workflow.getOutputDirectory(), "output-temp.csv");

				CsvWriter writer = new CsvWriter(outputTemp.getAbsolutePath(), "Technology", "repo count", "file count",
						"unique author count");

				merged = new HashMap<String, AnalysisResult>();
				merged.clear();

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

				if (output.exists())
					output.delete();
				outputTemp.renameTo(new File(workflow.getOutputDirectory(), "output.csv"));
				Files.move(Paths.get(outputTemp.getPath()),
						Paths.get(new File(workflow.getOutputDirectory(), "output.csv").getPath()), new CopyOption() {
						});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log(LogLevel.ERROR,
					"Exception occurred while flushing workflow output to disk. Message: " + ex.getMessage());
		} finally {
			flushing = false;
		}
	}
	// ----

}
