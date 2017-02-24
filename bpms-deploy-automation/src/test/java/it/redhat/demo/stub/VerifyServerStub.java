package it.redhat.demo.stub;

import java.util.HashMap;

import it.redhat.demo.pojo.KieContainerResourcePojo;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.pojo.ServiceResponsePojo;

public class VerifyServerStub implements WorkItemHandler {
	
	private int times = 0;

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<String, Object> results = new HashMap<>();
		
		ServiceResponsePojo serviceResponse = new ServiceResponsePojo();
		KieContainerResourcePojo kieContainerResource = new KieContainerResourcePojo();
		
		if (times < 3) {
			kieContainerResource.setStatus("CREATING");
			times++;
		} else {
			kieContainerResource.setStatus("STARTED");
		}
		
		serviceResponse.setResult(kieContainerResource);
		results.put("Result", serviceResponse);
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
