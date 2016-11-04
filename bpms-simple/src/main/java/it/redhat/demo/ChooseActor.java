package it.redhat.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

public class ChooseActor implements WorkItemHandler {
	
	private final TaskService taskService;
	
	public ChooseActor(RuntimeManager runtimeManager) {
		
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(null);
		this.taskService = runtimeEngine.getTaskService();
		
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		Map<String, Object> parameters = workItem.getParameters();
		
		long id = workItem.getProcessInstanceId();
		
		ArrayList<Status> status = new ArrayList<Status>();
		status.add(Status.Completed);
		
		List<TaskSummary> tasks = taskService.getTasksByStatusByProcessInstanceId(id, status, "");
		TaskSummary taskSummary = tasks.get(0);
		String actualOwnerId = taskSummary.getActualOwnerId();
		
		parameters.put("actor", actualOwnerId);
		
		manager.completeWorkItem(workItem.getId(), parameters);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
