package it.redhat.demo.migration;

import java.util.ArrayList;
import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.jaxb.JaxbProcessInstance;
import it.redhat.demo.jaxb.JaxbQueryProcessInstanceInfo;
import it.redhat.demo.jaxb.JaxbQueryProcessInstanceResult;

public class LoadProcessInstancesStub implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<String, Object> results = new HashMap<>();
		
		JaxbQueryProcessInstanceResult result = new JaxbQueryProcessInstanceResult();
		ArrayList<JaxbQueryProcessInstanceInfo> processInstanceInfoList = new ArrayList<>();
		JaxbQueryProcessInstanceInfo e = new JaxbQueryProcessInstanceInfo();
		JaxbProcessInstance processInstance = new JaxbProcessInstance();
		processInstance.setId(7l);
		e.setProcessInstance(processInstance);
		
		processInstanceInfoList.add(e);
		
		
		result.setProcessInstanceInfoList(processInstanceInfoList);
		
		results.put("Result", result);
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
