package it.redhat.test;

import java.util.HashMap;

import org.jbpm.process.workitem.rest.RESTWorkItemHandler;

import it.redhat.demo.customtask.ChooseDeployStrategy;
import it.redhat.demo.customtask.CreateContainerSpec;
import it.redhat.demo.customtask.InputValidator;

public class UnifiedManagedDeployIT extends UnifiedManagedDeployTest {

	@Override
	public void before() {
		
		runtimeManager = createRuntimeManager("it/redhat/test/create-container.bpmn2", "it/redhat/test/update-container.bpmn2", "it/redhat/test/migration.bpmn2", "it/redhat/test/unified-managed-deploy.bpmn2");
		
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler("fabio", "fabio$739", this.getClass().getClassLoader()));
		kieSession.getWorkItemManager().registerWorkItemHandler("ProcessServerRest", new RESTWorkItemHandler("fabio", "fabio$739", this.getClass().getClassLoader()));
		kieSession.getWorkItemManager().registerWorkItemHandler("ChooseDeployStrategy", new ChooseDeployStrategy());
		kieSession.getWorkItemManager().registerWorkItemHandler("CreateContainerSpec", new CreateContainerSpec());
		kieSession.getWorkItemManager().registerWorkItemHandler("InputValidator", new InputValidator());
		
	}
	
	@Override
	protected void testProcess(HashMap<String, Object> params) {
		kieSession.startProcess("it.redhat.test.unified-managed-deploy", params);
	}
	
}
