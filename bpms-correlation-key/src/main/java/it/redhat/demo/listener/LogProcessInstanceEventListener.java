package it.redhat.demo.listener;

import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LogProcessInstanceEventListener extends DefaultProcessEventListener {
	
	private static final Logger log = LoggerFactory.getLogger(LogProcessInstanceEventListener.class);
	
	// local cache
	private volatile Map<Long, String> correlationKeys = new HashMap<>();
	private RuntimeManager runtimeManager;

	public LogProcessInstanceEventListener(RuntimeManager runtimeManager) {
		this.runtimeManager = runtimeManager;
	}

	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent event) {
		
		// using for probing object instance scope
		log.info("Listener Identity Object Instance: [{}]", System.identityHashCode(this));
		
		Object newValue = event.getNewValue();
		Object oldValue = event.getOldValue();
		String variableId = event.getVariableId();
		ProcessInstance pi = event.getProcessInstance();
		String processId = pi.getProcessId();
		
		if (!correlationKeys.containsKey(pi.getId())) {
			log.info("search correlation key for process instance {}", pi.getId());
			addKey(event);
		} else {
			log.info("search correlation key for process instance {}", pi.getId());
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
			rootPi = LogProcessInstanceEventListener.findRoot(runtimeManager, (ProcessInstanceImpl) event.getProcessInstance());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		CorrelationKey correlationKey = (CorrelationKey) rootPi.getMetaData().get("CorrelationKey");
		if (correlationKey != null) {
			String externalForm = correlationKey.toExternalForm();
			correlationKeys.put(currentPi.getId(), externalForm);
		}
		
	}

	public static ProcessInstanceImpl findRoot(RuntimeManager rManager, ProcessInstanceImpl pi) {
		
		long parentProcessInstanceId = pi.getParentProcessInstanceId();
		if (parentProcessInstanceId <= 0) {
			return pi;
		}

		RuntimeEngine runtimeEngine = rManager.getRuntimeEngine(ProcessInstanceIdContext.get(parentProcessInstanceId));
		KieSession kieSession = runtimeEngine.getKieSession();


		ProcessInstanceImpl parentProcessInstance = (ProcessInstanceImpl) kieSession.getProcessInstance(parentProcessInstanceId);
		return LogProcessInstanceEventListener.findRoot(rManager, parentProcessInstance);
		
	}

}
