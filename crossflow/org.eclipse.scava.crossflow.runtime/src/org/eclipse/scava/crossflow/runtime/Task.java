package org.eclipse.scava.crossflow.runtime;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.scava.crossflow.runtime.utils.LogLevel;

import com.google.common.util.concurrent.ListenableFuture;

public abstract class Task {

	// Common configuration
	protected boolean cacheable = true;
	protected long timeout = 0;

	// Common Task instance state
	protected Set<String> currentRootIds = Collections.emptySet();
	protected Map<String, ListenableFuture<?>> currentRunnables = Collections.emptyMap();

	public abstract Workflow<?> getWorkflow();

	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}
	
	public long getTimeout() {
		return timeout;
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * Gets called upon workflow termination -- implementers of task should override
	 * this if they have any termination code to run
	 */
	public void close() {
		// implement any termination-specific functionality here
	};
	
	public boolean cancelJob(String id) {
		if (currentRunnables.containsKey(id)) {
			return currentRunnables.get(id).cancel(true);
		}
		return false;
	}

	/**
	 * Call this within consumeXYZ() to denote task blocked due to some reason
	 * 
	 * @param reason
	 */
	protected void taskBlocked(String reason) throws Exception {
		getWorkflow().setTaskBlocked(this, reason);
	}

	/**
	 * Call this within consumeXYZ() to denote task is now unblocked
	 * 
	 * @param reason
	 */
	protected void taskUnblocked() throws Exception {
		getWorkflow().setTaskUnblocked(this);
	}

	/**
	 * The ID of this task which follows the form "{task name}:{workflow name}"
	 * 
	 * @return the ID of this task
	 */
	public String getId() {
		return getName() + ":" + getWorkflow().getName();
	}

	/**
	 * Get the name of this Task
	 * 
	 * @return the name of this Task.
	 */
	public abstract String getName();

	public void log(LogLevel level, String message) {
		getWorkflow().logger.log(level, this, message);
	}
}
