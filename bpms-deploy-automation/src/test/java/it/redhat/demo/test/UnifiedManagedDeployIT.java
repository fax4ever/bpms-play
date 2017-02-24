package it.redhat.demo.test;

import java.util.HashMap;

import org.jbpm.process.workitem.rest.RESTWorkItemHandler;

import it.redhat.demo.customtask.ChooseDeployStrategy;
import it.redhat.demo.customtask.CreateContainerSpec;
import it.redhat.demo.customtask.InputValidator;
import it.redhat.demo.customtask.WaitTask;

public class UnifiedManagedDeployIT extends UnifiedManagedDeployTest {

	@Override
	public void before() {
		
		System.setProperty("org.kie.server.controller.user", "fabio");
		System.setProperty("org.kie.server.controller.pwd", "fabio$739");
		System.setProperty("org.kie.server.user", "fabio");
		System.setProperty("org.kie.server.pwd", "fabio$739");
		
		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH +"create-container.bpmn2", PROCESSES_BASE_PATH +"remove-container.bpmn2", PROCESSES_BASE_PATH +"migration.bpmn2", PROCESSES_BASE_PATH +"unified-managed-deploy.bpmn2");
		
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler(System.getProperty("org.kie.server.controller.user"), System.getProperty("org.kie.server.controller.pwd"), this.getClass().getClassLoader()));
		kieSession.getWorkItemManager().registerWorkItemHandler("ProcessServerRest", new RESTWorkItemHandler(System.getProperty("org.kie.server.user"), System.getProperty("org.kie.server.pwd"), this.getClass().getClassLoader()));
		kieSession.getWorkItemManager().registerWorkItemHandler("ChooseDeployStrategy", new ChooseDeployStrategy());
		kieSession.getWorkItemManager().registerWorkItemHandler("CreateContainerSpec", new CreateContainerSpec());
		kieSession.getWorkItemManager().registerWorkItemHandler("InputValidator", new InputValidator());
		kieSession.getWorkItemManager().registerWorkItemHandler("WaitTask", new WaitTask());
		
	}
	
	@Override
	protected void testProcess(HashMap<String, Object> params) {
		kieSession.startProcess("it.redhat.demo.unified-managed-deploy", params);
	}
	
}
