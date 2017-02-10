package it.redhat.demo;

import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoDomainSpecificTask implements WorkItemHandler {
	
	private static Logger log = LoggerFactory.getLogger(EchoDomainSpecificTask.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		Map<String, Object> parameters = workItem.getParameters();
		String input = (String) parameters.get("input");
		String constant = (String) parameters.get("constant");
		String expression = (String) parameters.get("expression");
		
		log.info("input value {}", input);
		log.info("constant value {}", constant);
		log.info("expression value {}", expression);
		
		parameters.put("output", input);
		manager.completeWorkItem(workItem.getId(), parameters);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
