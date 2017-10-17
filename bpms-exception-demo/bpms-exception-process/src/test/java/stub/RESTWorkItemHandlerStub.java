package stub;

import java.util.Map;
import java.util.Map.Entry;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class RESTWorkItemHandlerStub implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		Map<String, Object> parameters = workItem.getParameters();
		
		parameters.put("Result", "ciao");
		parameters.put("Status", "200");
		parameters.put("StatusMsg", "all right");
		
		for (Entry<String,Object> entry : parameters.entrySet()) {
			System.out.println(entry);
		}
		
		manager.completeWorkItem(workItem.getId(), parameters);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
