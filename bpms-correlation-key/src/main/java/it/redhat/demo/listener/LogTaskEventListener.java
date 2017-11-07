package it.redhat.demo.listener;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.User;

public class LogTaskEventListener extends DefaultTaskEventListener {
	
	private static final Logger logger = LoggerFactory.getLogger(LogTaskEventListener.class);

	private CorrelationKeyHolder keyHolder;

	public LogTaskEventListener(RuntimeManager runtimeManager, boolean perProcessInstanceStrategy) {
		this.keyHolder = new CorrelationKeyHolder( runtimeManager );
	}

	public LogTaskEventListener(boolean perProcessInstanceStrategy) {}

	public void setRuntimeManager(RuntimeManager runtimeManager) {
		this.keyHolder = new CorrelationKeyHolder( runtimeManager );
	}

	@Override
	public void afterTaskClaimedEvent(TaskEvent event) {
		Task task = event.getTask();

		TaskData taskData = task.getTaskData();
		long processInstanceId = taskData.getProcessInstanceId();

		String correlationKey = keyHolder.getCorrelationKey(processInstanceId);
		// skip logging if it is not possible to associate a correlation key
		if (correlationKey == null) {
			return;
		}

		User actualOwner = taskData.getActualOwner();

		logger.info("Task Claimed - correlationKey: [{}], taskName: [{}], processId: [{}], pi: [{}], actualOwner: [{}]",
				correlationKey, task.getName(), taskData.getProcessId(), processInstanceId, actualOwner.getId());
	}

	@Override
	public void beforeTaskCompletedEvent(TaskEvent event) {

		Task task = event.getTask();
		TaskData taskData = task.getTaskData();

		long processInstanceId = taskData.getProcessInstanceId();

		String correlationKey =keyHolder.getCorrelationKey(processInstanceId);
		// skip logging if it is not possible to associate a correlation key
		if (correlationKey == null) {
			return;
		}

		task = event.getTaskContext().loadTaskVariables(task);

		String processId = taskData.getProcessId();

		Map<String, Object> taskInputVariables = taskData.getTaskInputVariables();
		Map<String, Object> taskOutputVariables = taskData.getTaskOutputVariables();
		User actualOwner = taskData.getActualOwner();

		logger.info("Task Completed - correlationKey: [{}], taskName: [{}], processId: [{}], pi: [{}], in: [{}], out:[{}], actualOwner: [{}]",
				correlationKey, task.getName(), processId, processInstanceId, taskInputVariables, taskOutputVariables, actualOwner.getId());

	}
	
}
