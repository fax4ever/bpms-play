package it.redhat.demo.wih;

import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.model.ContainerKey;

public class ContainerHolder {
	
	private final static Logger LOG = LoggerFactory.getLogger( ContainerHolder.class );
	
	private static ContainerHolder instance;
	private final ConcurrentHashMap<ContainerKey, KieContainer> containers = new ConcurrentHashMap<ContainerKey, KieContainer>();
	private final KieServices kieServices = KieServices.Factory.get();

	private ContainerHolder() {}
	
	public static synchronized ContainerHolder getInstance() {
		if (instance == null) {
			instance = new ContainerHolder();
		}
		
		return instance;
	}

	public KieContainer getContainer( ContainerKey containerKey, long scannerInterval ) {
		
		if (containers.containsKey( containerKey )) {
			return containers.get( containerKey );
		}
		
		return tryToCreateContainer( containerKey, scannerInterval );
	}

	private synchronized KieContainer tryToCreateContainer(ContainerKey containerKey, long scannerInterval) {
		
		LOG.debug( "Try to create new container {}", containerKey );
		
		if (containers.containsKey( containerKey )) {
			
			LOG.debug( "Another thread create container {}", containerKey );
			return containers.get( containerKey );
		}
		
		LOG.debug( "Create new container {}", containerKey );
		KieContainer container = createContainer( containerKey, scannerInterval );
		
		containers.put( containerKey, container );
		return container;
		
	}

	private KieContainer createContainer(ContainerKey containerKey, long scannerInterval) {
		
		LOG.debug("About to create KieContainer for {} with scanner interval {}", containerKey, scannerInterval);
		KieContainer kieContainer = kieServices.newKieContainer(
				kieServices.newReleaseId(containerKey.getGroupId(), containerKey.getArtifactId(), containerKey.getVersion()));
		
		if (scannerInterval > 0) {
			KieScanner kieScanner = kieServices.newKieScanner(kieContainer);
		    kieScanner.start(scannerInterval);
		    LOG.debug("Scanner started for {} with poll interval set to {}", kieContainer, scannerInterval);
		}
		
		return kieContainer;
	}

}
