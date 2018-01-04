package it.redhat.demo.bpms.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jbpm.services.api.ProcessService;
import org.kie.server.services.api.KieServerApplicationComponentsService;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.api.SupportedTransports;

public class SafeSignalExtension implements KieServerApplicationComponentsService {

	@Override
	public Collection<Object> getAppComponents(String extension, SupportedTransports type, Object... services) {

		if ( !"jBPM".equals( extension ) ) {
			return Collections.emptyList();
		}

		ProcessService processService = null;
		KieServerRegistry context = null;

		for ( Object object : services ) {
			if ( object == null ) {
				continue;
			}
			else if ( KieServerRegistry.class.isAssignableFrom( object.getClass() ) ) {
				context = (KieServerRegistry) object;
				continue;
			}
			else if ( ProcessService.class.isAssignableFrom( object.getClass() ) ) {
				processService = (ProcessService) object;
				continue;
			}
		}

		SafeSignalService safeSignalService = new SafeSignalService( processService, context );

		List<Object> components = new ArrayList<>( 1 );
		if ( SupportedTransports.REST.equals( type ) ) {
			components.add( new SafeSignalResource( safeSignalService, context ) );
		}

		return components;

	}

}
