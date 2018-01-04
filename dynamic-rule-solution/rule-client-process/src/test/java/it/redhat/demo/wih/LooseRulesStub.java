package it.redhat.demo.wih;

import java.util.ArrayList;
import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.model.Hope;
import it.redhat.demo.model.Politician;

public class LooseRulesStub implements WorkItemHandler {
	
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		
		HashMap<String, Object> results = new HashMap<>();
		
		ArrayList<Object> politicians = new ArrayList<>();
		
		politicians.add( new Politician( "President of Umpa Lumpa", false ) );
		politicians.add( new Politician( "Prime Minster of Cheeseland", false ) );
		politicians.add( new Politician( "Tsar of Pringapopaloo", false ) );
		politicians.add( new Politician( "Omnipotence Om", false ) );
		politicians.add( new Hope() );
		
		results.put( "fact", politicians );
		
		workItemManager.completeWorkItem( workItem.getId(), results );
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		
		throw new UnsupportedOperationException();
		
	}

}
