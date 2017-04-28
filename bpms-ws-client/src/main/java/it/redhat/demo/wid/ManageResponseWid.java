package it.redhat.demo.wid;

import java.util.HashMap;
import java.util.List;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.ws.Employee;
import it.redhat.demo.ws.ExpenseResponse;

public class ManageResponseWid implements WorkItemHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ManageResponseWid.class);
	
	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		throw new UnsupportedOperationException("unsupported abort");
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<String, Object> output = new HashMap<>();
		
		ExpenseResponse response = (ExpenseResponse) workItem.getParameter("response");
		
		Employee employee = response.getEmployee();
		
		LOG.info("Employee NAME: {}", employee.getName());
		LOG.info("Employee SURNAME: {}", employee.getSurname());
		LOG.info("Employee RANK: {}", employee.getRank());
		
		response.getExpenses().stream().forEach(ex -> LOG.info("Amount: {}, Day: {}, Type: {}", ex.getAmount(), ex.getDay(), ex.getType()));

		output.put("response", response);
		
		manager.completeWorkItem(workItem.getId(), output);
		
	}

}
