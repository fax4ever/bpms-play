package it.redhat.demo.listener;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProcessEventListener extends DefaultProcessEventListener {
	
	private static final Logger log = LoggerFactory.getLogger(MyProcessEventListener.class);

	@Override
	public void afterNodeLeft(ProcessNodeLeftEvent event) {
		log.info("afterNodeLeft");
	}

	@Override
	public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
		log.info("afterNodeTriggered");
	}

	@Override
	public void afterProcessCompleted(ProcessCompletedEvent event) {
		log.info("afterProcessCompleted");
	}

	@Override
	public void afterProcessStarted(ProcessStartedEvent event) {
		log.info("afterProcessStarted");
	}

	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent event) {
		log.info("afterVariableChanged");
	}

	@Override
	public void beforeNodeLeft(ProcessNodeLeftEvent event) {
		log.info("beforeNodeLeft");
	}

	@Override
	public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
		log.info("beforeNodeTriggered");
	}

	@Override
	public void beforeProcessCompleted(ProcessCompletedEvent event) {
		log.info("beforeProcessCompleted");
	}

	@Override
	public void beforeProcessStarted(ProcessStartedEvent event) {
		log.info("beforeProcessStarted");
	}

	@Override
	public void beforeVariableChanged(ProcessVariableChangedEvent event) {
		log.info("beforeVariableChanged");
	}

}
