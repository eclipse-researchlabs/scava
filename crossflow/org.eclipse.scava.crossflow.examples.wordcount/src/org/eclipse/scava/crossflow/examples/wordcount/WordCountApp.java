/*******************************************************************************
 * Copyright (c) 2019 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributor(s):
 *      Patrick Neubauer - initial API and implementation
 ******************************************************************************/
package org.eclipse.scava.crossflow.examples.wordcount;

import java.io.File;

import org.eclipse.scava.crossflow.examples.wordcount.WordCountWorkflow;
import org.eclipse.scava.crossflow.runtime.FailedJob;
import org.eclipse.scava.crossflow.runtime.InternalException;
import org.eclipse.scava.crossflow.runtime.Mode;

/**
 * Standalone application for running a master and a set of workers.
 * 
 *  (!) Requires matching instance identifier (instanceId).
 * 
 * @author Patrick Neubauer
 * @author Konstantinos Barmpis
 *
 */
public class WordCountApp {
	
	public WordCountApp() throws Exception {
		WordCountWorkflow master = new WordCountWorkflow(Mode.MASTER);
		master.createBroker(true);
		master.setInputDirectory(new File("artifacts/in"));
		master.setOutputDirectory(new File("artifacts/out"));
		master.setInstanceId(WordCountProperties.INSTANCE_ID);
		master.setName("Master");
		
		WordCountWorkflow worker1 = new WordCountWorkflow(Mode.WORKER);
		worker1.setInstanceId(WordCountProperties.INSTANCE_ID);
		worker1.setName("Worker1");
		
		WordCountWorkflow worker2 = new WordCountWorkflow(Mode.WORKER);
		worker2.setInstanceId(WordCountProperties.INSTANCE_ID);
		worker2.setName("Worker2");
		
		master.run();
		worker1.run();
		worker2.run();
		
		master.awaitTermination();
		
		for (InternalException ex : master.getInternalExceptions()) {
			System.err.println(ex.getStacktrace());
		}
		
		for (FailedJob failed : master.getFailedJobs()) {
			System.err.println(failed.getStacktrace());
		}
	
		System.out.println("Done");
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		// setup and launch experiment
		WordCountApp app = new WordCountApp();
	}// main
	
}// WordCountApp