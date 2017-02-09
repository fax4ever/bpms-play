package it.redhat.demo;

import java.util.Collection;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ServerTemplate;

public class DefaultStrategyAndMergeMode implements WorkItemHandler {
	
	private static final String STRATEGY = "strategy";

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String parameter = (String) workItem.getParameter(STRATEGY);
		
		ServerTemplate serverTemplate = new ServerTemplate();
		Collection<ServerInstanceKey> serverInstanceKeys = serverTemplate.getServerInstanceKeys();
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
