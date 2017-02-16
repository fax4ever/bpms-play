package it.redhat.demo.customtask;

import java.util.HashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.dto.ContainerSpecDto;
import it.redhat.demo.dto.ReleaseIdDto;
import it.redhat.demo.dto.ServerTemplateDto;
import it.redhat.demo.model.MavenGavInfo;
import it.redhat.demo.model.MavenGavInfo.Affinity;

public class ChooseDeployStrategy implements WorkItemHandler {
	
	private static Logger log = LoggerFactory.getLogger(ChooseDeployStrategy.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		ServerTemplateDto serverTemplate = (ServerTemplateDto) workItem.getParameter("serverTemplate");
		String groupId = (String) workItem.getParameter("groupId");
		String artifactId = (String) workItem.getParameter("artifactId");
		String version = (String) workItem.getParameter("version");
		
		MavenGavInfo gav = new MavenGavInfo(groupId, artifactId, version);
		
		Affinity maxAffinity = Affinity.DIFFERENT_ARTIFACT;
		MavenGavInfo miniMigration = null;
		
		for (ContainerSpecDto container : serverTemplate.getContainersSpec()) {
			ReleaseIdDto releasedId = container.getReleasedId();
			
			MavenGavInfo deployGav = new MavenGavInfo(releasedId.getGroupId(), releasedId.getArtifactId(), releasedId.getVersion());
			Affinity affinity = gav.affinity(deployGav);
			
			if (Affinity.EQUALS.equals(affinity)) {
				maxAffinity = affinity;
				break;
			}
			
			if (Affinity.DIFFERNT_MINI.equals(affinity)) {
				if (miniMigration == null || deployGav.greterMini(miniMigration)) {
					miniMigration = deployGav;
				}
			}
			
			if (affinity.isGreater(maxAffinity)) {
				maxAffinity = affinity;
			}
				
		}
		
		HashMap<String,Object> resultsMap = new HashMap<>();
		
		if (Affinity.DIFFERNT_MINI.equals(maxAffinity) && gav.greterMini(miniMigration)) {
			resultsMap.put("update", false);
			resultsMap.put("migration", true);
			resultsMap.put("miniMigration", miniMigration);
			
			log.info("Performe Migration Deploy Strategy");
			
		} else if (Affinity.EQUALS.equals(maxAffinity)) {
			resultsMap.put("update", true);
			resultsMap.put("migration", false);
			
			log.info("Performe Snapshot Deploy Strategy");
			
		} else {
			resultsMap.put("update", false);
			resultsMap.put("migration", false);
			
			log.info("Performe Create New Deploy Strategy");
		}
		
		
		
		manager.completeWorkItem(workItem.getId(), resultsMap);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
