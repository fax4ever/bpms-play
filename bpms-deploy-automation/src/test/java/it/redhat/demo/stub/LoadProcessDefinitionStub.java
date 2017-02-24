package it.redhat.demo.stub;

import java.util.ArrayList;
import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.services.client.serialization.jaxb.impl.process.JaxbProcessDefinition;

import it.redhat.demo.jaxb.JaxbProcessDefinitionList;

public class LoadProcessDefinitionStub implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		JaxbProcessDefinitionList jaxbProcessDefinitionList = new JaxbProcessDefinitionList();
		ArrayList<JaxbProcessDefinition> processDefinitions = new ArrayList<JaxbProcessDefinition>();
		JaxbProcessDefinition jaxbProcessDefinition = new JaxbProcessDefinition();
		jaxbProcessDefinition.setId("it.redhat.demo.selection-process");
		
		processDefinitions.add(jaxbProcessDefinition);
		
		jaxbProcessDefinitionList.setProcessDefinitionList(processDefinitions);
		
		HashMap<String, Object> results = new HashMap<>();
		results.put("Result", jaxbProcessDefinitionList);
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
