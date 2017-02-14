package it.redhat.demo;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterDomainSpecificTask implements WorkItemHandler {
	
	private static Logger log = LoggerFactory.getLogger(ParameterDomainSpecificTask.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String bcHost = (String) workItem.getParameter("bcHost");
		String bcPort = (String) workItem.getParameter("bcPort");
		String serverId = (String) workItem.getParameter("serverId");
		String groupId = (String) workItem.getParameter("groupId");
		String artifactId = (String) workItem.getParameter("artifactId");
		String version = (String) workItem.getParameter("version");
		
		log.info("bcHost {}", bcHost);
		log.info("bcPort {}", bcPort);
		log.info("serverId {}", serverId);
		log.info("groupId {}", groupId);
		log.info("artifactId {}", artifactId);
		log.info("version {}", version);
		
		manager.completeWorkItem(workItem.getId(), new HashMap<>());
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
