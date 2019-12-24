package org.eclipse.scava.crossflow.tests;

import org.eclipse.scava.crossflow.runtime.serializer.tests.RuntimeSerializationTests;
import org.eclipse.scava.crossflow.runtime.tests.RuntimeTests;
import org.eclipse.scava.crossflow.tests.ack.AckWorkflowTests;
import org.eclipse.scava.crossflow.tests.addition.AdditionWorkflowCommandLineTests;
import org.eclipse.scava.crossflow.tests.addition.AdditionWorkflowTests;
import org.eclipse.scava.crossflow.tests.cache.DirectoryCacheCommandLineTests;
import org.eclipse.scava.crossflow.tests.cache.DirectoryCacheTests;
import org.eclipse.scava.crossflow.tests.commitment.CommitmentWorkflowTests;
import org.eclipse.scava.crossflow.tests.configurable.addition.ConfigurableWorkflowTests;
import org.eclipse.scava.crossflow.tests.crawler.CrawlerWorkflowTests;
import org.eclipse.scava.crossflow.tests.exceptions.ExceptionsWorkflowTests;
import org.eclipse.scava.crossflow.tests.matrix.MatrixWorkflowTests;
import org.eclipse.scava.crossflow.tests.minimal.MinimalWorkflowTests;
import org.eclipse.scava.crossflow.tests.multiflow.MultiflowTests;
import org.eclipse.scava.crossflow.tests.opinionated.OccurencesWorkflowTests;
import org.eclipse.scava.crossflow.tests.optionalbuiltinstream.OptionalBuiltinStreamTests;
import org.eclipse.scava.crossflow.tests.parallel.ParallelWorkflowTests;
import org.eclipse.scava.crossflow.tests.transactionalcaching.TransactionalCachingTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AdditionWorkflowTests.class,
	AdditionWorkflowCommandLineTests.class,
	DirectoryCacheTests.class,
	DirectoryCacheCommandLineTests.class,
	OccurencesWorkflowTests.class,
	CrawlerWorkflowTests.class,
	MatrixWorkflowTests.class,
	MultiflowTests.class,
	MinimalWorkflowTests.class,
	CommitmentWorkflowTests.class,
	ExceptionsWorkflowTests.class,
	AckWorkflowTests.class,
	ParallelWorkflowTests.class,
	TransactionalCachingTests.class,
	ConfigurableWorkflowTests.class,
	RuntimeTests.class,
	RuntimeSerializationTests.class,
	OptionalBuiltinStreamTests.class
})
public class CrossflowTests {

}
