package it.redhat.demo.listener;

import it.redhat.demo.correlation.CorrelationKeyTaskFinder;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LogTaskEventListener extends DefaultTaskEventListener {
	
	private static final Logger logger = LoggerFactory.getLogger(LogTaskEventListener.class);

	// local cache for correlation keys
	// key is the process instance ID
	// value is the correlation key
	// this map will be a single item map if the strategy is per process instance
	// volatile is useful only with singleton strategy
	// in the other strategies the listener object will not be shared with other threads
	private volatile Map<Long, String> correlationKeys = new HashMap<>();

	private CorrelationKeyTaskFinder finder;

	public LogTaskEventListener(RuntimeManager runtimeManager, boolean perProcessInstanceStrategy) {
		finder = new CorrelationKeyTaskFinder(perProcessInstanceStrategy, runtimeManager);
	}

	public LogTaskEventListener(boolean perProcessInstanceStrategy) {
		finder = new CorrelationKeyTaskFinder(perProcessInstanceStrategy);
	}

	public void setRuntimeManager(RuntimeManager runtimeManager) {
		this.finder.setRuntimeManager(runtimeManager);
	}

	@Override
	public void afterTaskClaimedEvent(TaskEvent event) {
		Task task = event.getTask();

		TaskData taskData = task.getTaskData();
		long processInstanceId = taskData.getProcessInstanceId();

		verifyAndFillCKMap(processInstanceId);
		String correlationKey = correlationKeys.get(processInstanceId);
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

		verifyAndFillCKMap(processInstanceId);
		String correlationKey = correlationKeys.get(processInstanceId);
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

	private void verifyAndFillCKMap(Long piid) {

		if (!correlationKeys.containsKey(piid)) {
			logger.trace("search correlation key for process instance {}", piid);
			String correlationKeyPis = finder.findCorrelationKey(piid);

			if (correlationKeyPis != null) {
				correlationKeys.put(piid, correlationKeyPis);
			}

		} else {
			logger.trace("cache correlation key for process instance {}", piid);
		}

	}
	
}
