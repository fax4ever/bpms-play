package it.redhat.demo.customtask;

import java.util.HashMap;

import it.redhat.demo.model.MavenGavInfo;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.dto.ContainerSpecDto;
import it.redhat.demo.dto.ReleaseIdDto;
import it.redhat.demo.dto.ServerTemplateDto;

public class ChooseDeployStrategy implements WorkItemHandler {
	
	private static Logger log = LoggerFactory.getLogger(ChooseDeployStrategy.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		ServerTemplateDto serverTemplate = (ServerTemplateDto) workItem.getParameter("serverTemplate");
		String groupId = (String) workItem.getParameter("groupId");
		String artifactId = (String) workItem.getParameter("artifactId");
		String version = (String) workItem.getParameter("version");
		
		MavenGavInfo gav = new MavenGavInfo(groupId, artifactId, version);
		
		MavenGavInfo.Affinity maxAffinity = MavenGavInfo.Affinity.DIFFERENT_ARTIFACT;
		MavenGavInfo miniMigration = null;
		
		for (ContainerSpecDto container : serverTemplate.getContainersSpec()) {
			ReleaseIdDto releasedId = container.getReleasedId();
			
			MavenGavInfo deployGav = new MavenGavInfo(releasedId.getGroupId(), releasedId.getArtifactId(), releasedId.getVersion());
			MavenGavInfo.Affinity affinity = gav.affinity(deployGav);
			
			if (MavenGavInfo.Affinity.EQUALS.equals(affinity)) {
				maxAffinity = affinity;
				break;
			}
			
			if (MavenGavInfo.Affinity.DIFFERNT_MINI.equals(affinity)) {
				if (miniMigration == null || deployGav.greterMini(miniMigration)) {
					miniMigration = deployGav;
				}
			}
			
			if (affinity.isGreater(maxAffinity)) {
				maxAffinity = affinity;
			}
				
		}
		
		HashMap<String,Object> resultsMap = new HashMap<>();
		
		if (MavenGavInfo.Affinity.DIFFERNT_MINI.equals(maxAffinity) && gav.greterMini(miniMigration)) {
			resultsMap.put("update", false);
			resultsMap.put("migration", true);
			resultsMap.put("newDeployment", gav.getGav());
			resultsMap.put("oldDeployment", miniMigration.getGav());
			
			log.info("Performe Migration Deploy Strategy");
			
		} else if (MavenGavInfo.Affinity.EQUALS.equals(maxAffinity)) {
			resultsMap.put("update", true);
			resultsMap.put("migration", false);
			resultsMap.put("newDeployment", gav.getGav());
			
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
