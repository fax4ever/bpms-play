package it.redhat.demo.main;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartProcessCommand implements Runnable {
	
	private static final int INCREASES_PER_TASK = 10;
	private static final Logger LOG = LoggerFactory.getLogger( StartProcessCommand.class );
	private static final String TARGET = "http://localhost:8080/kie-server/services/rest/server/containers/main/processes/it.redhat.demo.process.rule-client/instances";
	
	private final ResteasyClient client;

	public StartProcessCommand(ResteasyClient client) {
		this.client = client;
	}

	@Override
	public void run() {
		
		for ( int i = 0; i < INCREASES_PER_TASK; i++ ) {
			
			String id = client.target(TARGET)
				.request( MediaType.APPLICATION_JSON )
				.post( Entity.json("{}"), String.class );
				
			
			LOG.info( "PROCESS INSTANCE {} STARTED", id );
		}
		
	}
	
}
