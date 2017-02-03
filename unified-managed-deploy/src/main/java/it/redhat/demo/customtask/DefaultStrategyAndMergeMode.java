package it.redhat.demo.customtask;

import java.util.Collection;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;

public class DefaultStrategyAndMergeMode implements WorkItemHandler {
	
	private static final String STRATEGY = "strategy";

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String parameter = (String) workItem.getParameter(STRATEGY);
		
		/*
		ServerTemplate serverTemplate = new ServerTemplate();
		Collection<ServerInstanceKey> serverInstanceKeys = serverTemplate.getServerInstanceKeys();
		Collection<ContainerSpec> containersSpec = serverTemplate.getContainersSpec();
		
		ServiceResponse<KieContainerResource> serviceResponse = new ServiceResponse<>();
		KieContainerResource result = serviceResponse.getResult();
		*/
		
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
