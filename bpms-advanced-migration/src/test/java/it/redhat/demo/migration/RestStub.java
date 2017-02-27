package it.redhat.demo.migration;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class RestStub implements WorkItemHandler {
	
	private static final String DEPLOYMENT_PROCESS = "/business-central/rest/deployment";
	private static final String QUERY_RUNTIME_PROCESS = "/business-central/rest/query/runtime/process";
	
	private LoadProcessDefinitionStub loadProcessDefinitionStub = new LoadProcessDefinitionStub();
	private LoadProcessInstancesStub loadProcessInstancesStub = new LoadProcessInstancesStub();

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String url = (String) workItem.getParameter("Url");
		
		if (url.contains(DEPLOYMENT_PROCESS)) {
			loadProcessDefinitionStub.executeWorkItem(workItem, manager);
		} else if (url.contains(QUERY_RUNTIME_PROCESS)) {
			loadProcessInstancesStub.executeWorkItem(workItem, manager);
		} else {
			throw new RuntimeException("url not valid : " + url);
		}
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
