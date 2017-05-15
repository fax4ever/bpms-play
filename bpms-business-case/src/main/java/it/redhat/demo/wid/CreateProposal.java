package it.redhat.demo.wid;

import java.util.Date;
import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.entity.ProposalEntity;
import it.redhat.demo.entity.SubjectEntity;

public class CreateProposal implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<String,Object> results = new HashMap<>();
		
		ProposalEntity proposal = new ProposalEntity();
        proposal.setStart(new Date());
        proposal.setAmount(731);
        proposal.setAcquire(new Date());

        SubjectEntity subject = new SubjectEntity();
        subject.setTaxcode("237832788327sddss23");
        subject.setSurname("Ercoli");
        subject.setName("Fabio Masimo");
        subject.setBirth(new Date());

        proposal.setSubject(subject);
		
		results.put("proposal", proposal);
		
		manager.completeWorkItem(workItem.getId(), results);
		
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
