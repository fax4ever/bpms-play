package it.redhat.demo.listener;

import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LogTaskEventListener extends DefaultTaskEventListener {
	
	private static final Logger logger = LoggerFactory.getLogger(LogTaskEventListener.class);

	@Override
	public void afterTaskClaimedEvent(TaskEvent event) {
		Task task = event.getTask();
		TaskData taskData = task.getTaskData();

		logger.info("Task Claimed - taskName: [{}], processId: [{}], pi: [{}]",
				task.getName(), taskData.getProcessId(), taskData.getProcessInstanceId());
	}

	@Override
	public void afterTaskCompletedEvent(TaskEvent event) {

		Task task = event.getTask();

		task = event.getTaskContext().loadTaskVariables(task);
		TaskData taskData = task.getTaskData();

		long processInstanceId = taskData.getProcessInstanceId();
		String processId = taskData.getProcessId();

		Map<String, Object> taskInputVariables = taskData.getTaskInputVariables();
		Map<String, Object> taskOutputVariables = taskData.getTaskOutputVariables();

		logger.info("Task Completed - taskName: [{}], processId: [{}], pi: [{}], in: [{}], out: pi: [{}]",
				task.getName(), processId, processInstanceId, taskInputVariables, taskOutputVariables);

	}
	
}
