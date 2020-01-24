package org.eclipse.scava.crossflow.tests.concurrency;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.eclipse.scava.crossflow.tests.WorkflowTests;
import org.junit.Test;

public class ConcurrencyWorkflowTests extends WorkflowTests {

	@Test
	public void test_job_completes_when_no_timeout_specified() throws Exception {
		ConcurrencyWorkflow workflow = new ConcurrencyWorkflow();
		workflow.createBroker(createBroker);
		workflow.getSleepTimeSource().setSleepTimeNumbers(Arrays.asList(5));
		workflow.run();
		waitFor(workflow);
		assertEquals(1, workflow.getSleeper().getExecutions());
	}
	
	@Test
	public void test_job_fails_when_task_exceeds_timeout() throws Exception {
		ConcurrencyWorkflow workflow = new ConcurrencyWorkflow();
		workflow.createBroker(createBroker);
		workflow.getSleeper().setTimeout(5);
		workflow.getSleepTimeSource().setSleepTimeNumbers(Arrays.asList(500));
		workflow.run();
		waitFor(workflow);
		assertEquals(0, workflow.getSleeper().getExecutions());
	}

	@Test
	public void test_job_cancels_when_cancel_control_signal_received() throws Exception {
		ConcurrencyWorkflow workflow = new ConcurrencyWorkflow();
		workflow.createBroker(createBroker);
		workflow.getSleepTimeSource().setSleepTimeNumbers(Arrays.asList(500));
		workflow.run();

		String jobId = null;
		while(jobId == null) {
			if (!workflow.getSleeper().getJobIds().isEmpty()) {
				jobId = workflow.getSleeper().getJobIds().get(0);
			}
		}
		
		Thread.sleep(5000);
		workflow.cancelAllJobs(workflow.getSleeper().getJobIds().get(0));
		waitFor(workflow);
		
		assertEquals(0, workflow.getSleeper().getExecutions());
	}
	
}
