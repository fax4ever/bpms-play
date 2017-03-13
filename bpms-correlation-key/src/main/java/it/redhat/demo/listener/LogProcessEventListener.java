package it.redhat.demo.listener;

import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.internal.process.CorrelationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogProcessEventListener extends DefaultProcessEventListener {
	
	private static final Logger log = LoggerFactory.getLogger(LogProcessEventListener.class);

	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent event) {
		
		Object newValue = event.getNewValue();
		Object oldValue = event.getOldValue();
		String variableId = event.getVariableId();
		
		ProcessInstanceImpl currentPi = (ProcessInstanceImpl) event.getProcessInstance();
		ProcessInstanceImpl rootPi = LogProcessEventListener.findRoot(event.getKieRuntime(), currentPi);
		
		CorrelationKey correlationKey = (CorrelationKey) rootPi.getMetaData().get("CorrelationKey");
		String corrleationKeyString = "No correlation key";
		if (correlationKey != null) {
			corrleationKeyString = correlationKey.toExternalForm();
		}
		
		// using for probing object instance scope
		log.info("Listener Identity Object Instance: [{}]", System.identityHashCode(this));
		log.info("correlationKey: [{}], pi: [{}], root: [{}], variable: [{}], oldValue [{}], newValue: [{}]", 
				corrleationKeyString, currentPi.getId(), rootPi.getId(), variableId, oldValue, newValue);
		
	}
	
	public static ProcessInstanceImpl findRoot(KieRuntime kruntime, ProcessInstanceImpl pi) {
		
		long parentProcessInstanceId = pi.getParentProcessInstanceId();
		if (parentProcessInstanceId <= 0) {
			return pi;
		}
		
		ProcessInstanceImpl parentProcessInstance = (ProcessInstanceImpl) kruntime.getProcessInstance(parentProcessInstanceId, true);
		return LogProcessEventListener.findRoot(kruntime, parentProcessInstance);
		
	}

}
