package it.redhat.demo.stub;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.server.api.model.KieContainerStatus;

import it.redhat.demo.dto.ContainerSpecDto;
import it.redhat.demo.dto.ReleaseIdDto;
import it.redhat.demo.dto.ServerInstanceKeyDto;
import it.redhat.demo.dto.ServerTemplateDto;
import it.redhat.demo.dto.ServerTemplateKeyDto;

public class GetServerTemplateStub implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String serverId = (String) workItem.getParameter("serverId");
		
		HashMap<String, Object> results = new HashMap<>();
		
		ServerTemplateDto serverTemplate = new ServerTemplateDto(serverId, serverId);
		
		ServerInstanceKeyDto serverA = new ServerInstanceKeyDto("process-server", "process-server@localhost:8080", "process-server@localhost:8080", "http://localhost:8080/kie-server/services/rest/server");
		ServerInstanceKeyDto serverB = new ServerInstanceKeyDto("process-server", "process-server@localhost:8380", "process-server@localhost:8380", "http://localhost:8380/kie-server/services/rest/server");
		
		serverTemplate.addServerInstance(serverA);
		serverTemplate.addServerInstance(serverB);
		
		ServerTemplateKeyDto serverTemplateKey = new ServerTemplateKeyDto(serverId, serverId);
		ReleaseIdDto releaseId = new ReleaseIdDto("it.redhat.demo", "bpms-rest-task", "1.0.0-SNAPSHOT");
		ContainerSpecDto containerSpec = new ContainerSpecDto("it.redhat.demo:bpms-rest-task:1.0.0-SNAPSHOT", "it.redhat.demo:bpms-rest-task:1.0.0-SNAPSHOT", serverTemplateKey, releaseId, KieContainerStatus.STARTED, new HashMap<>());
		
		serverTemplate.addContainerSpec(containerSpec);
		
		results.put("Result", serverTemplate);
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
