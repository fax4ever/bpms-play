package it.redhat.demo.handler;

import java.util.HashMap;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class AdvancedWorkItemHandler implements WorkItemHandler {
	
	private RuntimeManager runtimeManager;
	
	public AdvancedWorkItemHandler(RuntimeManager runtimeManager) {
		
		this.runtimeManager = runtimeManager;
		// if I try to get runtime engine on constructor I will be an infinite loop!!
		// get runtime manager only
		
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		// get runtime engine on executeWorkItem method body "never in the constructor"
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get());
		
		KieSession kieSession = runtimeEngine.getKieSession();
		TaskService taskService = runtimeEngine.getTaskService();
		
		manager.completeWorkItem(workItem.getId(), new HashMap<>());
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
