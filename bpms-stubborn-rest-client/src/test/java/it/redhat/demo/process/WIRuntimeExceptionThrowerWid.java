package it.redhat.demo.process;

import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class WIRuntimeExceptionThrowerWid implements WorkItemHandler {

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		throw new WorkItemHandlerRuntimeException(new RuntimeException(), "my runtime exception");
	}

}
