package it.redhat.demo.wid;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.entity.ProposalEntity;

public class CreateProposal implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<Object,Object> results = new HashMap<>();
		
		ProposalEntity proposal = new ProposalEntity();
		
		
		results.put("proposal", new ProposalEntity());
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
