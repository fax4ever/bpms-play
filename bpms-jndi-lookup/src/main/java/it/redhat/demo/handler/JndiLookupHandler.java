package it.redhat.demo.handler;

import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JndiLookupHandler implements WorkItemHandler {
	
	private static Logger log = LoggerFactory.getLogger(JndiLookupHandler.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		HashMap<String,Object> results = new HashMap<>();
	
		try {
			InitialContext initialContext = new InitialContext();
			results.put("value", initialContext.lookup("java:/jboss/env/myresource"));
			results.put("status", true);
			
		} catch (NamingException e) {
			
			log.error(e.getMessage(), e);
			results.put("status", false);
		}
		
		log.info("status :: {}", results.get("status"));
		log.info("value :: {}", results.get("value"));
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		
	}

}
