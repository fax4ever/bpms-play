package it.redhat.demo;

import java.util.HashMap;
import java.util.HashSet;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class UpdateTimeWI implements WorkItemHandler {
	
	private final RuntimeManager runtimeManager;

	public UpdateTimeWI(RuntimeManager runtimeManager) {
		this.runtimeManager = runtimeManager;
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		RuntimeEngine engine = runtimeManager.getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		
		ksession.execute(new UpgradeCommand(workItem.getProcessInstanceId(), new HashSet<>()));
		
		workItemManager.completeWorkItem(workItem.getId(), new HashMap<>());
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		workItemManager.completeWorkItem(workItem.getId(), new HashMap<>());
	}

}
