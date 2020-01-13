package org.eclipse.scava.crossflow.tests.concurrency;

import java.util.LinkedList;
import java.util.List;

public class Sleeper extends SleeperBase {

	private int completions = 0;
	private List<String> jobIds = new LinkedList<>();

	@Override
	public Result consumeSleepTimes(SleepTime sleepTime) throws Exception {
		System.out.println("Received " + sleepTime.getJobId() + ":" + sleepTime.getSeconds());
		jobIds.add(sleepTime.getJobId());
		Thread.sleep(1000 * sleepTime.getSeconds());
		System.out.println("Finished");
		completions++;
		return null;
	}

	public List<String> getJobIds() {
		return jobIds;
	}

	public int getExecutions() {
		return completions;
	}

}
