package it.redhat.demo.migration;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.jaxb.JaxbQueryProcessInstanceResult;

public class MigrationTaskStub implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String oldDeployment = (String) workItem.getParameter("oldDeployment");
		String newDeployment = (String) workItem.getParameter("newDeployment");
		String processDefinition = (String) workItem.getParameter("processDefinition");
		JaxbQueryProcessInstanceResult processInstances = (JaxbQueryProcessInstanceResult) workItem.getParameter("processInstances");
		
		if (oldDeployment == null || oldDeployment.trim().isEmpty()) {
			throw new RuntimeException();
		}
		
		if (newDeployment == null || newDeployment.trim().isEmpty()) {
			throw new RuntimeException();
		}
		
		if (processDefinition == null || processDefinition.trim().isEmpty()) {
			throw new RuntimeException();
		}
		
		if (processInstances == null || processInstances.getProcessInstanceInfoList() == null || processInstances.getProcessInstanceInfoList().isEmpty()) {
			throw new RuntimeException();
		}
		
		manager.completeWorkItem(workItem.getId(), new HashMap<>());
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
