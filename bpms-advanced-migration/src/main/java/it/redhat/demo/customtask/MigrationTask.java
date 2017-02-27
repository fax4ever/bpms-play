package it.redhat.demo.customtask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.jbpm.kie.services.impl.admin.ProcessInstanceMigrationServiceImpl;
import org.jbpm.services.api.admin.MigrationEntry;
import org.jbpm.services.api.admin.MigrationReport;
import org.jbpm.services.api.admin.ProcessInstanceMigrationService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.jaxb.JaxbQueryProcessInstanceResult;

public class MigrationTask implements WorkItemHandler {
	
	private static Logger log = LoggerFactory.getLogger(MigrationTask.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String oldDeployment = (String) workItem.getParameter("oldDeployment");
		String newDeployment = (String) workItem.getParameter("newDeployment");
		String processDefinition = (String) workItem.getParameter("processDefinition");
		JaxbQueryProcessInstanceResult processInstances = (JaxbQueryProcessInstanceResult) workItem.getParameter("processInstances");
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> nodeMappging = (HashMap<String, String>) workItem.getParameter("nodeMappging");
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> processDefinitionMapping = (HashMap<String, String>) workItem.getParameter("processDefinitionMapping");
		
		HashMap<String, Object> results = new HashMap<>();
		List<MigrationReport> reports = new ArrayList<>();
		results.put("reports", reports);
		
		ProcessInstanceMigrationService migrationService = new ProcessInstanceMigrationServiceImpl();
		List<Long> processInstanceIds = processInstances.getProcessInstanceInfoList().stream().map(piInfo -> piInfo.getProcessInstance().getId()).collect(Collectors.toList());
		
		if (processInstanceIds.isEmpty()) {
			log.warn("no process instances to migrate from deployment {} with process definition {}", oldDeployment, processDefinition);
			manager.completeWorkItem(workItem.getId(), results);
			return;
		}
		
		// override target process definition
		if (processDefinitionMapping.containsKey(processDefinition)) {
			processDefinition = processDefinitionMapping.get(processDefinition);
		}
		
		reports = migrationService.migrate(oldDeployment, processInstanceIds, newDeployment, processDefinition, nodeMappging);
		for (MigrationReport report : reports) {
			boolean successful = report.isSuccessful();
			Date startDate = report.getStartDate();
			Date endDate = report.getEndDate();
			
			StringBuilder reportBuilder = new StringBuilder();
			reportBuilder.append("migration :: start {} end {} with outcome {} {");
			
			for (MigrationEntry migrationEntry : report.getEntries()) {
				reportBuilder.append("[");
				reportBuilder.append(migrationEntry.getMessage());
				reportBuilder.append("]");
			}
			
			reportBuilder.append("}");
			
			log.info(reportBuilder.toString(), startDate, endDate, successful);
		}
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
