package it.redhat.demo.customtask;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.exception.InvalidParameterRuntimeException;

public class InputValidator implements WorkItemHandler {
	
	private static final String STRING_SUFFIX = "s";
	private static Logger log = LoggerFactory.getLogger(InputValidator.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String bcHost = (String) workItem.getParameter("bcHost");
		String bcPort = (String) workItem.getParameter("bcPort");
		String serverId = (String) workItem.getParameter("serverId");
		String groupId = (String) workItem.getParameter("groupId");
		String artifactId = (String) workItem.getParameter("artifactId");
		String version = (String) workItem.getParameter("version");
		
		if (bcHost == null || bcHost.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid bcHost");
		}
		if (bcPort == null || bcPort.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid bcPort");
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
		
		// remove port string suffix
		// required for passing String parameter to Business Central Rest API
		if (bcPort.endsWith(STRING_SUFFIX)) {
			bcPort = bcPort.substring(0, bcPort.lastIndexOf(STRING_SUFFIX));
		}
		// remove release suffix
		// required for passing String parameter to Business Central Rest API
		if (version.endsWith(STRING_SUFFIX)) {
			version = version.substring(0, version.lastIndexOf(STRING_SUFFIX));
		}
		
		log.info("bcHost {}", bcHost);
		log.info("serverId {}", serverId);
		log.info("groupId {}", groupId);
		log.info("artifactId {}", artifactId);
		log.info("version {}", version);
		log.info("bcPort {}", bcPort);
		
		String bcUser = System.getProperty("org.kie.server.controller.user");
		if (bcUser == null || bcUser.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid bcUser");
		}
		
		String bcPass = System.getProperty("org.kie.server.controller.pwd");
		if (bcPass == null || bcPass.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid bcPass");
		}
		
		String psUser = System.getProperty("org.kie.server.user");
		if (psUser == null || psUser.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid psUser");
		}
		
		String psPass = System.getProperty("org.kie.server.pwd");
		if (psPass == null || psPass.trim().isEmpty()) {
			throw new InvalidParameterRuntimeException("invalid psPass");
		}
		
		HashMap<String, Object> results = new HashMap<>();
		
		results.put("bcPort", bcPort);
		results.put("version", version);
		
		manager.completeWorkItem(workItem.getId(), results);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}

}
