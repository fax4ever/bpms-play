package it.redhat.demo.wih;

import java.util.ArrayList;
import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.model.Politician;

public class CreatePoliticians implements WorkItemHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger( CreatePoliticians.class ); 

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		
		HashMap<String, Object> results = new HashMap<>();
		
		ArrayList<Object> politicians = new ArrayList<>();
		
		politicians.add( new Politician( "President of Umpa Lumpa", true ) );
		politicians.add( new Politician( "Prime Minster of Cheeseland", true ) );
		politicians.add( new Politician( "Tsar of Pringapopaloo", true ) );
		politicians.add( new Politician( "Omnipotence Om", true ) );
		
		LOG.info( "Create Politicians: {}", politicians );
		
		results.put( "politicians", politicians );
		
		workItemManager.completeWorkItem( workItem.getId(), results );
		
	}
	
	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		
		throw new UnsupportedOperationException();
		
	}

}
