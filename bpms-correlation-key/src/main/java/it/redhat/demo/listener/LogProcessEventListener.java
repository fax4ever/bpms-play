package it.redhat.demo.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * This class is not thread safe.
 * <p>
 * Listener instances are thread confined if the runtime strategy is PER_REQUEST or PER_PROCESS_INSTANCE.
 * <p>
 * Using this listener for PER_REQUEST or PER_PROCESS_INSTANCE strategies.
 */
public class LogProcessEventListener extends DefaultProcessEventListener {

	private static final Logger log = LoggerFactory.getLogger( LogProcessEventListener.class );
	private static final String[] SKIP_CHANGE_ON_THIS_VARIABLES = { "processId", "initiator" };

	private CorrelationKeyHolder keyHolder;

	private final Set<String> skippedVariableSet = new HashSet<>( Arrays.asList( SKIP_CHANGE_ON_THIS_VARIABLES ) );

	public LogProcessEventListener(RuntimeManager runtimeManager, boolean perProcessInstanceStrategy) {
		this.keyHolder = new CorrelationKeyHolder( runtimeManager );
	}

	public LogProcessEventListener(boolean perProcessInstanceStrategy) {
	}

	public void setRuntimeManager(RuntimeManager runtimeManager) {
		this.keyHolder = new CorrelationKeyHolder( runtimeManager );
	}

	@Override
	public void beforeProcessStarted(ProcessStartedEvent event) {

		ProcessInstance pi = event.getProcessInstance();
		String processId = pi.getProcessId();

		log.info(
			"Start Process - correlationKey: [{}], processId: [{}], pi: [{}], parent: [{}]",
			keyHolder.getCorrelationKey( pi.getId() ),
			processId,
			pi.getId(),
			pi.getParentProcessInstanceId()
		);

	}

	@Override
	public void afterProcessCompleted(ProcessCompletedEvent event) {

		ProcessInstance pi = event.getProcessInstance();
		String processId = pi.getProcessId();

		log.info(
			"End Process - correlationKey: [{}], processId: [{}], pi: [{}], parent: [{}]",
			keyHolder.getCorrelationKey( pi.getId() ),
			processId,
			pi.getId(),
			pi.getParentProcessInstanceId()
		);

	}

	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent event) {

		Object newValue = event.getNewValue();
		String variableId = event.getVariableId();

		// skip logging if the new value is null or an empty String
		if ( newValue == null || newValue.toString().trim().isEmpty() || skippedVariableSet.contains( variableId ) ) {
			return;
		}

		Object oldValue = event.getOldValue();
		ProcessInstance pi = event.getProcessInstance();
		String processId = pi.getProcessId();

		// skip logging if it is not possible to associate a correlation key
		if ( keyHolder.getCorrelationKey( pi.getId() ) == null ) {
			return;
		}

		log.info(
			"Change Variable - correlationKey: [{}], processId: [{}], pi: [{}], parent: [{}], variable: [{}], oldValue [{}], newValue: [{}]",
			keyHolder.getCorrelationKey( pi.getId() ),
			processId,
			pi.getId(),
			pi.getParentProcessInstanceId(),
			variableId,
			oldValue,
			newValue
		);

	}

}
