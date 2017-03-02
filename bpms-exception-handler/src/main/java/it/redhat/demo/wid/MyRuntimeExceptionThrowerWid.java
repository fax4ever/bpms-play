package it.redhat.demo.wid;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.excpetion.MyRuntimeException;

public class MyRuntimeExceptionThrowerWid implements WorkItemHandler {

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		String input = (String) workItem.getParameter("input");
		
		throw new MyRuntimeException("my custom excetion input: " + input);
	}

}
