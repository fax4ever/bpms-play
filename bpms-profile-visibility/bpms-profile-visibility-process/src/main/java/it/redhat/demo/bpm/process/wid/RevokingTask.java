package it.redhat.demo.bpm.process.wid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.context.EmptyContext;

public class RevokingTask implements WorkItemHandler {
	
	private final RuntimeManager runtimeManager;

	public RevokingTask(RuntimeManager runtimeManager) {
		this.runtimeManager = runtimeManager;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String agency = (String) workItem.getParameter("agency");
		
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine( EmptyContext.get() );
		
		TaskService taskService = runtimeEngine.getTaskService();
		List<Long> tasksByProcessInstanceId = taskService.getTasksByProcessInstanceId(workItem.getProcessInstanceId());
		
		// need to call "interal api" to reset the task instance
		
		manager.completeWorkItem(workItem.getId(), new HashMap<>());
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
