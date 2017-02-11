package it.redhat.test.stub;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class RestSmartStub implements WorkItemHandler {
	
	private GetServerTemplateStub getServerTemplate = new GetServerTemplateStub();

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String url = (String) workItem.getParameter("Url");
		String method = (String) workItem.getParameter("Method");
		
		if ("GET".equals(method) && url.contains("/business-central/rest/controller/management/servers/")) {
			getServerTemplate.executeWorkItem(workItem, manager);
		} else if ("POST".equals(method) && url.contains("/business-central/rest/deployment/")) {
			
		} else if ("PUT".equals(method) && url.contains("/business-central/rest/controller/management/servers/")) {
			
		} else if ("POST".equals(method) && url.contains("/business-central/rest/controller/management/servers/")) {
			
		}
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
