package it.redhat.demo.listener;

import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.kie.api.task.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTaskEventListener extends DefaultTaskEventListener {
	
	private static final Logger logger = LoggerFactory.getLogger(MyTaskEventListener.class);

	@Override
	public void beforeTaskStoppedEvent(TaskEvent event) {
		logger.info("beforeTaskStoppedEvent");
	}

	@Override
	public void beforeTaskExitedEvent(TaskEvent event) {
		logger.info("beforeTaskExitedEvent");
	}

	@Override
	public void afterTaskStartedEvent(TaskEvent event) {
		logger.info("afterTaskStartedEvent");
	}

	@Override
	public void afterTaskStoppedEvent(TaskEvent event) {
		logger.info("afterTaskStoppedEvent");
	}

	@Override
	public void afterTaskAddedEvent(TaskEvent event) {
		logger.info("afterTaskAddedEvent");
	}

	@Override
	public void beforeTaskActivatedEvent(TaskEvent event) {
		logger.info("beforeTaskActivatedEvent");
	}

	@Override
	public void beforeTaskClaimedEvent(TaskEvent event) {
		logger.info("beforeTaskClaimedEvent");
	}

	@Override
	public void beforeTaskSkippedEvent(TaskEvent event) {
		logger.info("beforeTaskSkippedEvent");
	}

	@Override
	public void beforeTaskStartedEvent(TaskEvent event) {
		logger.info("beforeTaskStartedEvent");
	}

	@Override
	public void beforeTaskCompletedEvent(TaskEvent event) {
		logger.info("beforeTaskCompletedEvent");
	}

	@Override
	public void beforeTaskFailedEvent(TaskEvent event) {
		logger.info("beforeTaskFailedEvent");
	}

	@Override
	public void beforeTaskAddedEvent(TaskEvent event) {
		logger.info("beforeTaskAddedEvent");
	}

	@Override
	public void beforeTaskReleasedEvent(TaskEvent event) {
		logger.info("beforeTaskReleasedEvent");
	}

	@Override
	public void beforeTaskResumedEvent(TaskEvent event) {
		logger.info("beforeTaskResumedEvent");
	}

	@Override
	public void beforeTaskSuspendedEvent(TaskEvent event) {
		logger.info("beforeTaskSuspendedEvent");
	}

	@Override
	public void beforeTaskForwardedEvent(TaskEvent event) {
		logger.info("beforeTaskForwardedEvent");
	}

	@Override
	public void beforeTaskDelegatedEvent(TaskEvent event) {
		logger.info("beforeTaskDelegatedEvent");
	}

	@Override
	public void beforeTaskNominatedEvent(TaskEvent event) {
		logger.info("beforeTaskNominatedEvent");
	}

	@Override
	public void afterTaskActivatedEvent(TaskEvent event) {
		logger.info("afterTaskActivatedEvent");
	}

	@Override
	public void afterTaskClaimedEvent(TaskEvent event) {
		logger.info("afterTaskClaimedEvent");
	}

	@Override
	public void afterTaskSkippedEvent(TaskEvent event) {
		logger.info("afterTaskClaimedEvent");
	}

	@Override
	public void afterTaskCompletedEvent(TaskEvent event) {
		logger.info("afterTaskCompletedEvent");
	}

	@Override
	public void afterTaskFailedEvent(TaskEvent event) {
		logger.info("afterTaskFailedEvent");
	}

	@Override
	public void afterTaskExitedEvent(TaskEvent event) {
		logger.info("afterTaskExitedEvent");
	}

	@Override
	public void afterTaskReleasedEvent(TaskEvent event) {
		logger.info("afterTaskReleasedEvent");
	}

	@Override
	public void afterTaskResumedEvent(TaskEvent event) {
		logger.info("afterTaskResumedEvent");
	}

	@Override
	public void afterTaskSuspendedEvent(TaskEvent event) {
		logger.info("afterTaskSuspendedEvent");
	}

	@Override
	public void afterTaskForwardedEvent(TaskEvent event) {
		logger.info("afterTaskForwardedEvent");
	}

	@Override
	public void afterTaskDelegatedEvent(TaskEvent event) {
		logger.info("afterTaskDelegatedEvent");
	}

	@Override
	public void afterTaskNominatedEvent(TaskEvent event) {
		logger.info("afterTaskNominatedEvent");
	}
	
}
