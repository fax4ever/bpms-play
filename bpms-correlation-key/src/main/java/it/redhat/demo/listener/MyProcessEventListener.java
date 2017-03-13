package it.redhat.demo.listener;

import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.internal.process.CorrelationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProcessEventListener extends DefaultProcessEventListener {
	
	private static final Logger log = LoggerFactory.getLogger(MyProcessEventListener.class);

	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent event) {
		
		Object newValue = event.getNewValue();
		Object oldValue = event.getOldValue();
		String variableId = event.getVariableId();
		String variableInstanceId = event.getVariableInstanceId();
		
		ProcessInstanceImpl currentPi = (ProcessInstanceImpl) event.getProcessInstance();
		ProcessInstanceImpl rootPi = MyProcessEventListener.findRoot(event.getKieRuntime(), currentPi);
		
		CorrelationKey correlationKey = (CorrelationKey) rootPi.getMetaData().get("CorrelationKey");
		String corrleationKeyString = "No correlation key";
		if (correlationKey != null) {
			corrleationKeyString = correlationKey.toExternalForm();
		}
		
		log.info("correlationKey {}, variableId {}, variableInstanceId {}, oldValue {}, newValue {}, processInstance {}, rootProcessInstance {}", corrleationKeyString, variableId, variableInstanceId, oldValue, newValue, currentPi.getId(), rootPi.getId());
		
	}
	
	public static ProcessInstanceImpl findRoot(KieRuntime kruntime, ProcessInstanceImpl pi) {
		
		long parentProcessInstanceId = pi.getParentProcessInstanceId();
		if (parentProcessInstanceId <= 0) {
			return pi;
		}
		
		ProcessInstanceImpl parentProcessInstance = (ProcessInstanceImpl) kruntime.getProcessInstance(parentProcessInstanceId, true);
		return MyProcessEventListener.findRoot(kruntime, parentProcessInstance);
		
	}

}
