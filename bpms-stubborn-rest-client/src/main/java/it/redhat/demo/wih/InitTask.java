package it.redhat.demo.wih;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class InitTask implements WorkItemHandler {
	
	private static final String MAX_ATTEMPTS_PROPERTY = "it.redhat.demo.stubborn.rest.client.maxAttempts";
	private static final String INITIAL_DELAY_IN_SECONDS_PROPERTY = "it.redhat.demo.stubborn.rest.client.initialDelayInSeconds";

	private static final int MAX_ATTEMPTS_DEFAULT = 5;
	private static final int INITIAL_DELAY_IN_SECONDS_DEFAULT = 5;
	private static final String CONTENT_TYPE_DEFAULT = "application/json";
	
	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		
		Integer maxAttempts = (Integer) workItem.getParameter(ProcessInstanceVariable.MAX_ATTEMPTS);
		Integer initialDelayInSeconds = (Integer) workItem.getParameter(ProcessInstanceVariable.INITIAL_DELAY_IN_SECONDS);
		String contentType = (String) workItem.getParameter(ProcessInstanceVariable.CONTENT_TYPE);
		
		String maxAttemptsProp = System.getProperty(MAX_ATTEMPTS_PROPERTY);
		String initialDelayInSecondsProp = System.getProperty(INITIAL_DELAY_IN_SECONDS_PROPERTY);
		
		if (maxAttempts == null && maxAttemptsProp != null) {
			maxAttempts = Integer.parseInt(maxAttemptsProp);
		} else if (maxAttempts == null && maxAttemptsProp == null) {
			maxAttempts = MAX_ATTEMPTS_DEFAULT;
		}
		
		if (initialDelayInSeconds == null && initialDelayInSecondsProp != null) {
			initialDelayInSeconds = Integer.parseInt(initialDelayInSecondsProp);
		} else if (initialDelayInSeconds == null && initialDelayInSecondsProp == null) {
			initialDelayInSeconds = INITIAL_DELAY_IN_SECONDS_DEFAULT;
		}
		
		if (contentType == null) {
			contentType = CONTENT_TYPE_DEFAULT;
		}
		
		HashMap<String, Object> results = new HashMap<>();
		results.put(ProcessInstanceVariable.NUM_ATTEMPTS, 0);
		results.put(ProcessInstanceVariable.MAX_ATTEMPTS, maxAttempts);
		results.put(ProcessInstanceVariable.INITIAL_DELAY_IN_SECONDS, initialDelayInSeconds);
		results.put(ProcessInstanceVariable.CONTENT_TYPE, contentType);
		
		workItemManager.completeWorkItem(workItem.getId(), results);
		
	}

}
