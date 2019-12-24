package org.eclipse.scava.crossflow.runtime.utils;

import java.io.Serializable;
import java.time.Instant;

import org.eclipse.scava.crossflow.runtime.Workflow;

/**
 * A logging message sent within a workflow.
 * <p>
 * Messages are comprised of the following components:
 * <ul>
 * <li>level - logging level as defined by {@link LogLevel}</li>
 * <li>timestamp - time which this message was created, can be overriden</li>
 * <li>instanceId - the Instance ID of the sending workflow</li>
 * <li>workflow - the name of the Workflow object that sent the message</li>
 * <li>task - (optional) the name of the task that sent the message</li>
 * <li>message - the message body</li>
 * </ul>
 * <p>
 * The default formatted String given by {@link #toString()} is:
 * 
 * <pre>
 * 2011-12-03T10:15:30Z [LEVEL] instance:workflow(:task) message
 * </pre>
 */
public class LogMessage implements Serializable {

	private static final long serialVersionUID = 2L;

	LogLevel level;
	long timestamp;
	String instanceId = null;
	String workflow = null;
	String task = null;
	String message = null;
	
	public LogMessage() {
		this.level = LogLevel.DEBUG;
		this.timestamp = System.currentTimeMillis();
	}

	public LogMessage(Workflow<?> workflow) {
		this();
		this.instanceId = workflow.getInstanceId();
		this.workflow = workflow.getName();
	}

	public LogLevel getLevel() {
		return level;
	}

	public LogMessage level(LogLevel level) {
		this.level = level;
		return this;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public LogMessage timestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public LogMessage instanceId(String instanceId) {
		this.instanceId = instanceId;
		return this;
	}

	public String getWorkflow() {
		return workflow;
	}

	public LogMessage workflow(String workflow) {
		this.workflow = workflow;
		return this;
	}

	public String getTask() {
		return task;
	}

	public LogMessage task(String task) {
		this.task = task;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public LogMessage message(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return Instant.ofEpochMilli(timestamp).toString() + " [" + level + "] " + toExternalLoggerString();
	}

	/**
	 * Same as {@link LogMessage#toString()} but without the timestamp or level
	 * pre-fixed. Useful for use with external logging frameworks
	 * 
	 * @return {@link #toString()} without timestamp or level prefixed
	 */
	public String toExternalLoggerString() {
		return instanceId + ":" + workflow + (task == null ? "" : (":" + task)) + " | " + message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((workflow == null) ? 0 : workflow.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogMessage other = (LogMessage) obj;
		if (instanceId == null) {
			if (other.instanceId != null)
				return false;
		} else if (!instanceId.equals(other.instanceId))
			return false;
		if (level != other.level)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (workflow == null) {
			if (other.workflow != null)
				return false;
		} else if (!workflow.equals(other.workflow))
			return false;
		return true;
	}
	
}
