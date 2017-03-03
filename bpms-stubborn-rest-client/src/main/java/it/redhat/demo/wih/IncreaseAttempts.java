package it.redhat.demo.wih;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class IncreaseAttempts implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		Integer numAttempts = (Integer) workItem.getParameter(ProcessInstanceVariable.NUM_ATTEMPTS);
		Integer initialDelayInSeconds = (Integer) workItem.getParameter(ProcessInstanceVariable.INITIAL_DELAY_IN_SECONDS);
		
		numAttempts++;
		
		HashMap<String, Object> results = new HashMap<>();
		results.put(ProcessInstanceVariable.NUM_ATTEMPTS, numAttempts);
		results.put(ProcessInstanceVariable.DELAY_ATTEMPTS, numAttempts*initialDelayInSeconds+"s");
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
