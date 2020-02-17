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

import org.eclipse.scava.crossflow.runtime.BuiltinStreamConsumer;
import org.eclipse.scava.crossflow.runtime.DirectoryCache;
import org.eclipse.scava.crossflow.runtime.FailedJob;
import org.eclipse.scava.crossflow.runtime.InternalException;
import org.eclipse.scava.crossflow.runtime.Mode;
import org.eclipse.scava.crossflow.runtime.serialization.XstreamSerializer;
import org.eclipse.scava.crossflow.runtime.utils.TaskStatus;

public class TechAnalysisApp {

	public static void main(String[] args) throws Exception {

		// CloneUtils.removeRepoClones(TechAnalysisProperties.CLONE_PARENT_DESTINATION);

		TechnologyAnalysis master = new TechnologyAnalysis(Mode.MASTER_BARE);
		//
		master.setCache(new DirectoryCache(new File("cache")));
		master.registerCustomSerializationTypes(new XstreamSerializer());
		//
		master.setInputDirectory(new File("resources/in"));
		master.setOutputDirectory(new File("resources/out"));
		master.setName("Master");
		master.setInstanceId("TechAnalysis");
		master.createBroker(false);
		master.enableTaskMetadataTopic(false);
		master.enableStreamMetadataTopic(false);

		TechnologyAnalysis worker1 = new TechnologyAnalysis(Mode.WORKER);
		worker1.setName("Worker1");
		worker1.setInstanceId("TechAnalysis");

		TechnologyAnalysis worker2 = new TechnologyAnalysis(Mode.WORKER);
		worker2.setName("Worker2");
		worker2.setInstanceId("TechAnalysis");

		TechnologyAnalysis worker3 = new TechnologyAnalysis(Mode.WORKER);
		worker3.setName("Worker3");
		worker3.setInstanceId("TechAnalysis");

		TechnologyAnalysis worker4 = new TechnologyAnalysis(Mode.WORKER);
		worker4.setName("Worker4");
		worker4.setInstanceId("TechAnalysis");

		master.run();
		worker1.run();
		worker2.run();
		worker3.run();
		worker4.run();

		while (!master.hasTerminated()) {
			Thread.sleep(100);
		}

		System.out.println("Done");

		System.exit(0);
	}

}
