package it.redhat.demo.bpms.wid;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.bpms.model.VariablesWrapper;

public class UnwrapVariables implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		VariablesWrapper variablesWrapper = (VariablesWrapper) workItem.getParameter(WrapVariables.VARIABLES_WRAPPER_VARIABLE_NAME);
		
		manager.completeWorkItem(workItem.getId(), variablesWrapper.getParameters());
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
