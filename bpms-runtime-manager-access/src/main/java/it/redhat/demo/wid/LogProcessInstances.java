package it.redhat.demo.wid;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogProcessInstances implements WorkItemHandler {
	
	private final static Logger LOG = LoggerFactory.getLogger(LogProcessInstances.class);

	@Override
	public void abortWorkItem(WorkItem workitem, WorkItemManager manager) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void executeWorkItem(WorkItem workitem, WorkItemManager manager) {
		
		String containerId = (String) workitem.getParameter("containerId");
		
		RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(containerId);
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get());
		
		AuditService auditService = runtimeEngine.getAuditService();
		List<Long> ids = auditService.findProcessInstances().stream()
			.map(pi -> pi.getProcessId())
			.map(stringId -> Long.parseLong(stringId))
			.collect(Collectors.toList());
		
		LOG.info("process instance ids: {}", ids);
		
		HashMap<String, Object> results = new HashMap<>();
		results.put("processInstanceIds", ids);
		
		manager.completeWorkItem(workitem.getId(), results);
		
	}

}
