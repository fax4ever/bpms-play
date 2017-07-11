package it.redhat.demo.stub;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogProcessInstancesStub implements WorkItemHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(LogProcessInstancesStub.class);

	@Override
	public void abortWorkItem(WorkItem workitem, WorkItemManager manager) {
		
	}

	@Override
	public void executeWorkItem(WorkItem workitem, WorkItemManager manager) {
		
		String containerId = (String) workitem.getParameter("containerId");
		
		if (containerId == null) {
			throw new RuntimeException("container id not present in the request");
		}
	
		HashMap<String, Object> results = new HashMap<>();
		Long[] ids = {1l, 2l, 3l};
		List<Long> asList = Arrays.asList(ids);
		
		LOG.info("processInstanceIds {}", asList);
		results.put("processInstanceIds", asList);
		
		manager.completeWorkItem(workitem.getId(), results);
		
	}
	
	
}
