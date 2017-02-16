package it.redhat.demo.customtask;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.spec.Capability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.dto.ContainerSpecDto;
import it.redhat.demo.dto.ProcessConfigDto;
import it.redhat.demo.dto.ReleaseIdDto;
import it.redhat.demo.dto.ServerTemplateDto;
import it.redhat.demo.dto.ServerTemplateKeyDto;

public class CreateContainerSpec implements WorkItemHandler {
	
	private static Logger log = LoggerFactory.getLogger(CreateContainerSpec.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		ServerTemplateDto serverTemplate = (ServerTemplateDto) workItem.getParameter("serverTemplate");
		
		log.info("Target Server Template " + serverTemplate);
		
		String groupId = (String) workItem.getParameter("groupId");
		String artifactId = (String) workItem.getParameter("artifactId");
		String version = (String) workItem.getParameter("version");
		String serverId = (String) workItem.getParameter("serverId");
		String gav = groupId + ":" + artifactId + ":" + version;
		
		ServerTemplateKeyDto serverTemplateKey = new ServerTemplateKeyDto(serverId, serverId);
		ReleaseIdDto releaseId = new ReleaseIdDto(groupId, artifactId, version);
		ProcessConfigDto processConfig = new ProcessConfigDto();
		processConfig.setRuntimeStrategy("PER_PROCESS_INSTANCE");
		processConfig.setMergeMode("MERGE_COLLECTIONS");
		
		ContainerSpecDto container = new ContainerSpecDto();
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
		
		log.info("Deploy new container " + container);
		log.info("On server lists " + processServerUrls);
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
