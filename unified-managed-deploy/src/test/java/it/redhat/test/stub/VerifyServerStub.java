package it.redhat.test.stub;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ServiceResponse;

public class VerifyServerStub implements WorkItemHandler {
	
	private int times = 0;

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<String, Object> results = new HashMap<>();
		
		ServiceResponse<KieContainerResource> serviceResponse = new ServiceResponse<KieContainerResource>();
		KieContainerResource kieContainerResource = new KieContainerResource();
		
		if (times < 3) {
			kieContainerResource.setStatus(KieContainerStatus.CREATING);
			times++;
		} else {
			kieContainerResource.setStatus(KieContainerStatus.STARTED);
		}
		
		serviceResponse.setResult(kieContainerResource);
		results.put("Result", serviceResponse);
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
