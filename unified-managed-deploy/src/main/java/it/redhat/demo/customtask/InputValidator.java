package it.redhat.demo.customtask;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.exception.InvalidParameterRuntimeException;

public class InputValidator implements WorkItemHandler {
	
	private static Logger log = LoggerFactory.getLogger(InputValidator.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String bcHost = (String) workItem.getParameter("bcHost");
		String serverId = (String) workItem.getParameter("serverId");
		String groupId = (String) workItem.getParameter("groupId");
		String artifactId = (String) workItem.getParameter("artifactId");
		String version = (String) workItem.getParameter("version");
		
		String bcPort = (String) workItem.getParameter("bcPort");
		Integer bcPortInt = (Integer) workItem.getParameter("bcPortInt");
		
		// setting bcPort if pass as integer value
		if (bcPortInt != null) {
			bcPort = bcPortInt.toString();
		}
		
		if (bcHost == null || bcHost.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid bcHost");
		}
		if (serverId == null || serverId.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid serverId");
		}
		if (groupId == null || groupId.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid groupId");
		}
		if (artifactId == null || artifactId.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid artifactId");
		}
		if (version == null || version.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid version");
		}
		if (bcPort == null || bcPort.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid bcPort and bcPortInt");
		}
		
		log.info("bcHost {}", bcHost);
		log.info("serverId {}", serverId);
		log.info("groupId {}", groupId);
		log.info("artifactId {}", artifactId);
		log.info("version {}", version);
		log.info("bcPort {}", bcPort);
		
		HashMap<String, Object> results = new HashMap<>();
		
		results.put("bcPort", bcPort);
		
		manager.completeWorkItem(workItem.getId(), results);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}

}
