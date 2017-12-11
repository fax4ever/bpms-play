package it.workshop.stub;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.workshop.model.Rata;

public class CaricaRataStub implements WorkItemHandler {

	private final Rata rata;

	public CaricaRataStub(boolean isTheLast, Integer amount) {
		this.rata = new Rata(isTheLast, amount);
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<String, Object> results = new HashMap<>();
		
		results.put("Result", rata);
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		throw new UnsupportedOperationException("The wid is not abortable");
		
	}

}
