package it.redhat.demo.remove;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.stub.NoActionWorkItemHandlerStub;

public class RemoveRestStub implements WorkItemHandler {
	
	private final static String BC_REST_DEPLOYMENT = "/business-central/rest/deployment/";
	private final static String BC_REST_CONTROLLER = "/business-central/rest/controller/";
	
	private NoActionWorkItemHandlerStub noActionWid = new NoActionWorkItemHandlerStub(); 

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<String, Object> results = new HashMap<>();
		
		String url = (String) workItem.getParameter("Url");
		String method = (String) workItem.getParameter("Method");
		
		System.out.println(url);
		System.out.println(method);
		
		if (!method.equals("GET")) {
			
			noActionWid.executeWorkItem(workItem, manager);
			return;
			
		}
		
		if (url.contains(BC_REST_DEPLOYMENT)) {
			
			results.put("Result", " ... UNDEPLOYED ... ");
			manager.completeWorkItem(workItem.getId(), results);
			return;
			
		}
		
		if (url.contains(BC_REST_CONTROLLER)) {
			
			results.put("Status", "404");
			manager.completeWorkItem(workItem.getId(), results);
			return;
			
		}
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
