package it.redhat.demo.wid;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
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
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get());

		KieSession kieSession = runtimeEngine.getKieSession();

		Collection<ProcessInstance> processInstances = kieSession.getProcessInstances();
		List<Long> ids = processInstances.stream().map(processInstance -> processInstance.getId()).collect(Collectors.toList());

		LOG.info("process instance ids (kie-session): {}", ids);

		AuditService auditService = runtimeEngine.getAuditService();
		
		ids = auditService.findProcessInstances().stream()
			.map(pi -> pi.getProcessInstanceId())
			.collect(Collectors.toList());
		
		LOG.info("process instance ids (audit): {}", ids);
		
		HashMap<String, Object> results = new HashMap<>();
		results.put("processInstanceIds", ids);
		
		manager.completeWorkItem(workitem.getId(), results);
		
	}

}
