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
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogProcessInstanceLocal implements WorkItemHandler {
	
	private final static Logger LOG = LoggerFactory.getLogger(LogProcessInstanceLocal.class);
	
	private final RuntimeManager runtimeManager;

	public LogProcessInstanceLocal(RuntimeManager runtimeManager) {
		this.runtimeManager = runtimeManager;
	}

	@Override
	public void executeWorkItem(WorkItem workitem, WorkItemManager manager) {
		
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get());
		AuditService auditService = runtimeEngine.getAuditService();
		
		List<Long> ids = auditService.findProcessInstances().stream()
			.map(pi -> pi.getProcessInstanceId())
			.collect(Collectors.toList());
		
		LOG.info("process instance ids: {}", ids);
		
		HashMap<String, Object> results = new HashMap<>();
		results.put("processInstanceIds", ids);
		
		manager.completeWorkItem(workitem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workitem, WorkItemManager manager) {
		throw new UnsupportedOperationException();
	}

}
