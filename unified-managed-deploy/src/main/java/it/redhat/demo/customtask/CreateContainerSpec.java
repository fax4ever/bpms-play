package it.redhat.demo.customtask;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;

public class CreateContainerSpec implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String groupId = (String) workItem.getParameter("groupId");
		String artifactId = (String) workItem.getParameter("artifactId");
		String version = (String) workItem.getParameter("version");
		String serverId = (String) workItem.getParameter("serverId");
		String gav = groupId + ":" + artifactId + ":" + version;
		
		ServerTemplateKey serverTemplate = new ServerTemplateKey(serverId, serverId);
		ReleaseId releaseId = new ReleaseId(groupId, artifactId, version);
		ProcessConfig processConfig = new ProcessConfig();
		processConfig.setRuntimeStrategy("PER_PROCESS_INSTANCE");
		processConfig.setMergeMode("MERGE_COLLECTIONS");
		
		ContainerSpec container = new ContainerSpec();
		container.setId(gav);
		container.setContainerName(gav);
		container.setServerTemplateKey(serverTemplate);
		container.setReleasedId(releaseId);
		container.setStatus(KieContainerStatus.STARTED);
		container.addConfig(Capability.PROCESS, processConfig);
		
		HashMap<String,Object> results = new HashMap<>();
		results.put("psDeployRequest", container);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
