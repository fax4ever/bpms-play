package it.redhat.demo.listener;

import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.process.CorrelationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LogProcessEventListener extends DefaultProcessEventListener {
	
	private static final Logger log = LoggerFactory.getLogger(LogProcessEventListener.class);
	
	// local cache
	private volatile Map<Long, String> correlationKeys = new HashMap<>();

	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent event) {
		
		// using for probing object instance scope
		log.info("Listener Identity Object Instance: [{}]", System.identityHashCode(this));
		
		Object newValue = event.getNewValue();

		if (newValue == null || newValue.toString().trim().isEmpty()) {
			return;
		}

		Object oldValue = event.getOldValue();
		String variableId = event.getVariableId();
		ProcessInstance pi = event.getProcessInstance();
		String processId = pi.getProcessId();
		
		if (!correlationKeys.containsKey(pi.getId())) {
			log.info("search correlation key for process instance {}", pi.getId());
			addKey(event);
		} else {
			log.info("found correlation key for process instance {}", pi.getId());
		}
		
		String correlationKey = correlationKeys.get(pi.getId());
		if (correlationKey == null) {
			correlationKey = "No Correlation Key";
		}
	
		log.info("correlationKey: [{}], processId: [{}], pi: [{}], parent: [{}], variable: [{}], oldValue [{}], newValue: [{}]", 
				correlationKey, processId, pi.getId(), pi.getParentProcessInstanceId(), variableId, oldValue, newValue);
		
	}
	
	private void addKey(ProcessVariableChangedEvent event) {
		
		ProcessInstance currentPi = event.getProcessInstance();
		ProcessInstanceImpl rootPi = (ProcessInstanceImpl) event.getProcessInstance();
		
		try {
			rootPi = LogProcessEventListener.findRoot(event.getKieRuntime(), (ProcessInstanceImpl) event.getProcessInstance());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		CorrelationKey correlationKey = (CorrelationKey) rootPi.getMetaData().get("CorrelationKey");
		if (correlationKey != null) {
			String externalForm = correlationKey.toExternalForm();
			correlationKeys.put(currentPi.getId(), externalForm);
		}
		
	}

	public static ProcessInstanceImpl findRoot(KieRuntime kruntime, ProcessInstanceImpl pi) {
		
		long parentProcessInstanceId = pi.getParentProcessInstanceId();
		if (parentProcessInstanceId <= 0) {
			return pi;
		}
		
		ProcessInstanceImpl parentProcessInstance = (ProcessInstanceImpl) kruntime.getProcessInstance(parentProcessInstanceId);
		return LogProcessEventListener.findRoot(kruntime, parentProcessInstance);
		
	}

}
