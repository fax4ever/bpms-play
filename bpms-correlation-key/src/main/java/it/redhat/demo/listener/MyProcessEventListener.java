package it.redhat.demo.listener;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProcessEventListener extends DefaultProcessEventListener {
	
	private static final Logger log = LoggerFactory.getLogger(MyProcessEventListener.class);

	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent event) {
		
		Object newValue = event.getNewValue();
		Object oldValue = event.getOldValue();
		ProcessInstance processInstance = event.getProcessInstance();
		String variableId = event.getVariableId();
		String variableInstanceId = event.getVariableInstanceId();
		
		log.info("variableId {}, variableInstanceId {}, oldValue {}, newValue {}, processInstance {}", variableId, variableInstanceId, oldValue, newValue, processInstance);
		
	}

}
