package it.redhat.demo.bpm.process.wid;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.bpm.process.entity.Ownership;
import it.redhat.demo.bpm.process.exception.CipOrAgencyCodeNotValidException;

public class ChangeOwnership implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String cip = (String) workItem.getParameter("cip");
		String agency = (String) workItem.getParameter("agency");
		
		if (cip == null || agency == null || cip.trim().isEmpty() || agency.trim().isEmpty()) {
			throw new CipOrAgencyCodeNotValidException(cip, agency);
		}
		
		HashMap<String,Object> results = new HashMap<>();
		results.put("owner", new Ownership(cip, agency));
		
		manager.completeWorkItem(workItem.getId(), results);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		throw new RuntimeException("ChangeOwnership is not abortable task");
	}

}
