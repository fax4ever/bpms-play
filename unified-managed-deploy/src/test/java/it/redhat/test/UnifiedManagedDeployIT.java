package it.redhat.test;

import org.jbpm.process.workitem.rest.RESTWorkItemHandler;

import it.redhat.demo.customtask.ChooseDeployStrategy;
import it.redhat.demo.customtask.CreateContainerSpec;
import it.redhat.demo.customtask.InputValidator;

public class UnifiedManagedDeployIT extends UnifiedManagedDeployTest {

	@Override
	protected void registerWorkItemHandler() {
		
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler("fabio", "fabio$739", this.getClass().getClassLoader()));
		kieSession.getWorkItemManager().registerWorkItemHandler("ProcessServerRest", new RESTWorkItemHandler("fabio", "fabio$739", this.getClass().getClassLoader()));
		kieSession.getWorkItemManager().registerWorkItemHandler("ChooseDeployStrategy", new ChooseDeployStrategy());
		kieSession.getWorkItemManager().registerWorkItemHandler("CreateContainerSpec", new CreateContainerSpec());
		kieSession.getWorkItemManager().registerWorkItemHandler("InputValidator", new InputValidator());
		
	}

}
