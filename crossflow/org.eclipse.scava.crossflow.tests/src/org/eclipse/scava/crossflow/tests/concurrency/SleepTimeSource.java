package org.eclipse.scava.crossflow.tests.concurrency;

import java.util.LinkedList;
import java.util.List;

public class SleepTimeSource extends SleepTimeSourceBase {
	
	protected List<Integer> sleepTimeNumbers = new LinkedList<>();
	
	
	@Override
	public void produce() throws Exception {
		for (Integer i : sleepTimeNumbers) {
			sendToSleepTimes(new SleepTime(i));
		}
	}
	
	public void setSleepTimeNumbers(List<Integer> timeoutNumbers) {
		this.sleepTimeNumbers = timeoutNumbers;
	}

}
