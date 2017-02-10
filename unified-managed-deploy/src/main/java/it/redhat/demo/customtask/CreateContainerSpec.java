package it.redhat.demo.customtask;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;

public class CreateContainerSpec implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		ServerTemplate serverTemplate = (ServerTemplate) workItem.getParameter("serverTemplate");
		String groupId = (String) workItem.getParameter("groupId");
		String artifactId = (String) workItem.getParameter("artifactId");
		String version = (String) workItem.getParameter("version");
		String serverId = (String) workItem.getParameter("serverId");
		String gav = groupId + ":" + artifactId + ":" + version;
		
		ServerTemplateKey serverTemplateKey = new ServerTemplateKey(serverId, serverId);
		ReleaseId releaseId = new ReleaseId(groupId, artifactId, version);
		ProcessConfig processConfig = new ProcessConfig();
		processConfig.setRuntimeStrategy("PER_PROCESS_INSTANCE");
		processConfig.setMergeMode("MERGE_COLLECTIONS");
		
		ContainerSpec container = new ContainerSpec();
		container.setId(gav);
		container.setContainerName(gav);
		container.setServerTemplateKey(serverTemplateKey);
		container.setReleasedId(releaseId);
		container.setStatus(KieContainerStatus.STARTED);
		container.addConfig(Capability.PROCESS, processConfig);
		
		List<String> processServerUrls = serverTemplate.getServerInstanceKeys().stream().map(key -> key.getUrl()).collect(Collectors.toList());
		
		HashMap<String,Object> results = new HashMap<>();
		results.put("psDeployRequest", container);
		results.put("processServerUrls", processServerUrls);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
