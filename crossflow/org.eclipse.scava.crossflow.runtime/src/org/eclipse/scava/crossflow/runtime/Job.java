package org.eclipse.scava.crossflow.runtime;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * A Job is a single unit of work, containing the data needed by a {@link Task}
 * to execute.
 */
public class Job implements Serializable {

	private static final long serialVersionUID = 431981L;

	/**
	 * Unique ID identifying the specific instance of the Job
	 */
	protected String jobId;

	/**
	 * ID identifying the source of the Job for caching purposes
	 */
	protected String correlationId;

	/**
	 * A set of IDs used to identify the source/root Job where this instance of Job
	 * was spawned from. f {@code null} then this is a root Job i.e. the input
	 * source of the workflow
	 */
	protected Set<String> rootIds;

	protected String destination;
	protected boolean cached = false;
	protected int failures = 0;
	protected boolean cacheable = true;
	protected long timeout = 0;
	// sets whether this job requires a transactional level of caching (usually due
	// to being created multiple times per single task)
	protected boolean transactional = true;
	// denotes that this job is a simple message denoting success of a transaction
	// (with this correlationId and to totalOutputs # of channels)
	private boolean isTransactionSuccessMessage = false;
	private int totalOutputs = 0;

	public Job() {
		this.jobId = UUID.randomUUID().toString();
	}

	public String toString() {
		return jobId + " " + correlationId + " " + destination + " " + cacheable + " " + failures;
	}

	public boolean isTransactional() {
		return transactional;
	}

	public void setTransactional(boolean transactional) {
		this.transactional = transactional;
	}

	@Deprecated
	public String getId() {
		return getJobId();
	}

	@Deprecated
	public void setId(String id) {
		setJobId(id);
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public Set<String> getRootIds() {
		if (rootIds == null)
			return Collections.emptySet();
		return Collections.unmodifiableSet(rootIds);
	}

	public void addRootId(String rootId) {
		if (rootIds == null)
			rootIds = new HashSet<>();
		rootIds.add(rootId);
	}
	
	public void addRootId(Collection<String> rootIds) {
		if (this.rootIds == null) this.rootIds = new HashSet<>();
		this.rootIds.addAll(rootIds);
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDestination() {
		return destination;
	}

	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	public int getFailures() {
		return failures;
	}

	public void setFailures(int failures) {
		this.failures = failures;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Deprecated
	public String getXML() {
		final String jobId = this.jobId;
		final int failures = this.getFailures();
		final String correlationId = this.correlationId;
		final Set<String> rootIds = this.rootIds;
		final boolean cached = this.cached;
		final boolean cacheable = this.cacheable;

		this.jobId = null;
		this.correlationId = null;
		this.rootIds = null;
		this.failures = 0;
		this.cached = false;
		this.cacheable = true;

		String xml = new XStream(new DomDriver()).toXML(this);

		this.jobId = jobId;
		this.correlationId = correlationId;
		this.rootIds = rootIds;
		this.cached = cached;
		this.failures = failures;
		this.cacheable = cacheable;

		return xml;
	}

	public String getHash() {
		// FIXME if two outputs have the same signature (aka if a task outputs two
		// identical elements) then duplicates are lost!
		return UUID.nameUUIDFromBytes(getXML().getBytes()).toString();
	}

	public boolean isTransactionSuccessMessage() {
		return isTransactionSuccessMessage;
	}

	public void setIsTransactionSuccessMessage(boolean isTransactionSuccessMessage) {
		this.isTransactionSuccessMessage = isTransactionSuccessMessage;
	}

	public int getTotalOutputs() {
		return totalOutputs;
	}

	public void setTotalOutputs(int totalOutputs) {
		this.totalOutputs = totalOutputs;
	}

}
