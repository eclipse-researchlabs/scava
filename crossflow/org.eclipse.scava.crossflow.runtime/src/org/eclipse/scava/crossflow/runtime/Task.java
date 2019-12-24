package org.eclipse.scava.crossflow.runtime;

import org.eclipse.scava.crossflow.runtime.utils.LogLevel;

public abstract class Task {

	protected boolean cacheable = true;

	public abstract Workflow<?> getWorkflow();

	public void log(LogLevel level, String message) {
		getWorkflow().logger.log(level, this, message);
	}

	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	/**
	 * Gets called upon workflow termination -- implementers of task should override
	 * this if they have any termination code to run
	 */
	public void close() {
		// implement any termination-specific functionality here
	};

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
}
