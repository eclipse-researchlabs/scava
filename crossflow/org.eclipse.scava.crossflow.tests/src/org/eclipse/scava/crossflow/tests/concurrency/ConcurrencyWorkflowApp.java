package org.eclipse.scava.crossflow.tests.concurrency;

import org.eclipse.scava.crossflow.runtime.Mode;

public class ConcurrencyWorkflowApp {

	public static void main(String[] args) throws Exception {
		
		ConcurrencyWorkflow master = new ConcurrencyWorkflow(Mode.MASTER);
		master.createBroker(true);
		master.setMaster("localhost");
		
		//master.setParallelization(4);
		
		//master.setInputDirectory(new File("experiment/in"));
		//master.setOutputDirectory(new File("experiment/out"));
		
		master.setInstanceId("Example ConcurrencyWorkflow Instance");
		master.setName("ConcurrencyWorkflow");
		
		master.run();
		
		master.awaitTermination();
		
		System.out.println("Done");
		
	}
	
}
