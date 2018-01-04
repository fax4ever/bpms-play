package it.redhat.demo.bpms.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.bpms.command.VerifySignalCommand;
import it.redhat.demo.bpms.exception.SignalNotAccepted;
import org.jbpm.services.api.ProcessService;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.impl.marshal.MarshallerHelper;

public class SafeSignalService {

	private static final Logger LOG = LoggerFactory.getLogger( SafeSignalService.class );

	private final ProcessService processService;
	private final MarshallerHelper marshallerHelper;

	public SafeSignalService(ProcessService processService, KieServerRegistry context) {
		this.processService = processService;
		this.marshallerHelper = new MarshallerHelper( context );
	}

	public void signalProcessInstance(String containerId, Number processInstanceId, String signalName,
			String eventPayload, String marshallingType) throws SignalNotAccepted {

		LOG.debug( "About to unmarshal event from payload: '{}'", eventPayload );
		Object event = marshallerHelper.unmarshal( containerId, eventPayload, marshallingType, Object.class );

		Boolean execute = processService
				.execute( containerId, ProcessInstanceIdContext.get( processInstanceId.longValue() ),
					  new VerifySignalCommand( processInstanceId.longValue(), signalName, event )
				);

		LOG.debug( "Verify Signal Output: '{}'", execute );

		if (!execute) {
			SignalNotAccepted signalNotAccepted = new SignalNotAccepted( processInstanceId.longValue(), signalName );
			LOG.warn( signalNotAccepted.getMessage() );
			throw signalNotAccepted;
		}

		LOG.debug(
				"Calling Signal '{}' process instance with id {} on container {} and event {}", signalName,
				processInstanceId, containerId, event
		);
		processService.signalProcessInstance( processInstanceId.longValue(), signalName, event );

	}

}
