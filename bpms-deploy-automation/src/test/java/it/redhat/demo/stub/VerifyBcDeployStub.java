package it.redhat.demo.stub;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class VerifyBcDeployStub implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<String, Object> results = new HashMap<>();
		results.put("Result", "<deployment-unit> <groupId>it.redhat.demo</groupId> <artifactId>bpms-selection-process</artifactId> <version>1.0.0</version> <strategy>PER_PROCESS_INSTANCE</strategy> <status>DEPLOYED</status> </deployment-unit>");
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}

}
