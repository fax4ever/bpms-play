package it.redhat.test.stub;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;

public class GetServerTemplateStub implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String serverId = (String) workItem.getParameter("serverId");
		
		HashMap<String, Object> results = new HashMap<>();
		
		ServerTemplate serverTemplate = new ServerTemplate(serverId, serverId);
		
		ServerInstanceKey serverA = new ServerInstanceKey("process-server", "process-server@localhost:8080", "process-server@localhost:8080", "http://localhost:8080/kie-server/services/rest/server");
		ServerInstanceKey serverB = new ServerInstanceKey("process-server", "process-server@localhost:8380", "process-server@localhost:8380", "http://localhost:8380/kie-server/services/rest/server");
		
		serverTemplate.addServerInstance(serverA);
		serverTemplate.addServerInstance(serverB);
		
		ServerTemplateKey serverTemplateKey = new ServerTemplateKey(serverId, serverId);
		ReleaseId releaseId = new ReleaseId("it.redhat.demo", "bpms-rest-task", "1.0.0-SNAPSHOT");
		ContainerSpec containerSpec = new ContainerSpec("it.redhat.demo:bpms-rest-task:1.0.0-SNAPSHOT", "it.redhat.demo:bpms-rest-task:1.0.0-SNAPSHOT", serverTemplateKey, releaseId, KieContainerStatus.STARTED, new HashMap<>());
		
		serverTemplate.addContainerSpec(containerSpec);
		
		results.put("Result", serverTemplate);
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
