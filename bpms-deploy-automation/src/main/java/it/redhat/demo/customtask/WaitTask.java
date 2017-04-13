package it.redhat.demo.customtask;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class WaitTask implements WorkItemHandler {
	
	private Logger log = LoggerFactory.getLogger(WaitTask.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		manager.completeWorkItem(workItem.getId(), new HashMap<>());
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}

}
