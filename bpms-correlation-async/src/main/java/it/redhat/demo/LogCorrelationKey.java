package it.redhat.demo;

import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.process.CorrelationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class LogCorrelationKey implements WorkItemHandler {
	
	private static Logger log = LoggerFactory.getLogger(LogCorrelationKey.class);
	
	private KieSession kieSession;
	
	public LogCorrelationKey(KieSession kieSession) {
		this.kieSession = kieSession;
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {

		long piId = workItem.getProcessInstanceId();
		ProcessInstanceImpl processInstance = (ProcessInstanceImpl) kieSession.getProcessInstance(piId);

		CorrelationKey correlationKey = (CorrelationKey) processInstance.getMetaData().get("CorrelationKey");
		if (correlationKey != null) {
			log.info("[process instance {}] found correlation key {}", piId, correlationKey.toExternalForm());
		} else {
			log.warn("[process instance {}] correlation key is not present", piId);
		}

		workItemManager.completeWorkItem(workItem.getId(), new HashMap<>());
		
	}

}
