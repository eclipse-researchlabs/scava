package org.eclipse.scava.crossflow.tests.configurable.addition;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eclipse.scava.crossflow.runtime.Mode;
import org.eclipse.scava.crossflow.tests.WorkflowTests;
import org.junit.Test;

public class ConfigurableWorkflowTests extends WorkflowTests {

	@Test
	public void testOutput() throws Exception {
		AdditionWorkflow workflow = new AdditionWorkflow();
		workflow.createBroker(createBroker);
		workflow.getNumberPairSource().setNumbers(Arrays.asList(1, 2));
		workflow.setTerminationTimeout(0);
		workflow.run();
		waitFor(workflow);
		assertArrayEquals(new Integer[] { 2, 4 }, workflow.getAdditionResultsSink().getNumbers().toArray());
	}

	@Test
	public void testLateWorkerAddition() throws Exception {

		AdditionWorkflow masterWorkflow = new AdditionWorkflow(Mode.MASTER, 1);
		masterWorkflow.setName("master");
		masterWorkflow.setInstanceId("testLateWorkerAddition");
		masterWorkflow.createBroker(createBroker);
		masterWorkflow.getNumberPairSource().setNumbers(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
		masterWorkflow.setTerminationTimeout(0);
		masterWorkflow.getNumberPairSource().setInterval(600);
		masterWorkflow.run();

		Thread.sleep(1000);

		AdditionWorkflow workerWorkflow = new AdditionWorkflow(Mode.WORKER, 1);
		workerWorkflow.setName("worker");
		workerWorkflow.setInstanceId("testLateWorkerAddition");
		workerWorkflow.setTerminationTimeout(0);
		workerWorkflow.run(500);

		waitFor(masterWorkflow);
		waitFor(workerWorkflow);

		assertTrue(workerWorkflow.getAdder().getExecutions() < 10);
		assertEquals(10, masterWorkflow.getAdder().getExecutions() + workerWorkflow.getAdder().getExecutions());

	}

}
