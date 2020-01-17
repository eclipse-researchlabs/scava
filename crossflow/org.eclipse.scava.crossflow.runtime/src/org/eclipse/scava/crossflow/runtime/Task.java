package org.eclipse.scava.crossflow.runtime;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.eclipse.scava.crossflow.runtime.utils.LogLevel;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

public abstract class Task {

	// Common configuration
	protected boolean cacheable = true;
	protected long timeout = 0;

	// Common Task instance state
	protected Map<? extends Job, ListenableFuture<?>> activeRunnables = Collections.emptyMap();
	protected Set<String> activeRootIds = Collections.emptySet();

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

	/**
	 * Attempts to cancel an active Job based on the given payload.
	 * <p>
	 * By default this method performs cancellation based on Job ID matching i.e.
	 * cancel if payload==activeJob.getJobId().
	 * <p>
	 * Subclasses are free to override this method as they see fit
	 * 
	 * @param payload payload included by control signal message
	 * @return whether the job cancelled successfully
	 */
	public boolean cancelJob(String payload) {
		if (getActiveJob() == null || !Objects.equal(payload, getActiveJob().getJobId())) {
			return false;
		} else {
			return getActiveRunnable().cancel(true);
		}
	}

	/**
	 * 
	 * @param <J> Type of the {@link Job}
	 * @return the active {@link Job} being processed otherwise {@code null}
	 */
	@SuppressWarnings("unchecked")
	protected <J extends Job> J getActiveJob() {
		return (J) Iterables.getOnlyElement(activeRunnables.keySet(), null);
	}

	/**
	 * @return the active {@link Future}/{@link Runnable} being executed by this
	 *         Task otherwise {@code null}
	 */
	protected Future<?> getActiveRunnable() {
		return Iterables.getOnlyElement(activeRunnables.values(), null);
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
