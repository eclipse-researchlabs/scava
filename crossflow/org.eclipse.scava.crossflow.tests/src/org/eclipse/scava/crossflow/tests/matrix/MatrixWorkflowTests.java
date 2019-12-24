package org.eclipse.scava.crossflow.tests.matrix;

import static org.junit.Assert.assertEquals;
import org.eclipse.scava.crossflow.runtime.DirectoryCache;
import org.eclipse.scava.crossflow.tests.WorkflowTests;
import org.junit.Test;

public class MatrixWorkflowTests extends WorkflowTests {

	@Test
	public void testOutput() throws Exception {

		MatrixWorkflow workflow = new MatrixWorkflow();
		workflow.createBroker(createBroker);
		workflow.getMatrixConfigurationSource().setNumberOfConfigurations(2);
		workflow.run();
		waitFor(workflow);

		assertEquals(2, workflow.getMatrixSink().getMatrices().size());
		assertEquals(1, workflow.getMatrixSink().getMatrices().get(0).getRows().size());
		assertEquals(2, workflow.getMatrixSink().getMatrices().get(1).getRows().size());

	}

	@Test
	public void testCache() throws Exception {

		MatrixWorkflow workflow = new MatrixWorkflow();
		workflow.createBroker(createBroker);
		/*
		 * DirectoryCache cache = new DirectoryCache( new
		 * File("/Users/dkolovos/Desktop/matrix-cache") );
		 */
		DirectoryCache cache = new DirectoryCache();
		workflow.setCache(cache);
		workflow.getMatrixConfigurationSource().setNumberOfConfigurations(2);
		workflow.run();
		waitFor(workflow);

		workflow = new MatrixWorkflow();
		workflow.createBroker(createBroker);
		workflow.setCache(new DirectoryCache(cache.getDirectory()));
		workflow.getMatrixConfigurationSource().setNumberOfConfigurations(3);
		workflow.run();
		waitFor(workflow);

		assertEquals(3, workflow.getMatrixSink().getMatrices().size());
		assertEquals(1, workflow.getMatrixConstructor().getExecutions());

	}
}
