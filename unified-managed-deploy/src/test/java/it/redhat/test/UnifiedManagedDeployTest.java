package it.redhat.test;

import java.util.HashMap;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;

import it.redhat.demo.customtask.ChooseDeployStrategy;
import it.redhat.demo.customtask.CreateContainerSpec;
import it.redhat.test.stub.RestStub;

public class UnifiedManagedDeployTest extends JbpmJUnitBaseTestCase {
	
	private KieSession kieSession;
	
	public UnifiedManagedDeployTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		createRuntimeManager("it/redhat/test/create-container.bpmn2", "it/redhat/test/update-container.bpmn2", "it/redhat/test/migration.bpmn2", "it/redhat/test/unified-managed-deploy.bpmn2");
		
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RestStub());
		kieSession.getWorkItemManager().registerWorkItemHandler("ChooseDeployStrategy", new ChooseDeployStrategy());
		kieSession.getWorkItemManager().registerWorkItemHandler("CreateContainerSpec", new CreateContainerSpec());
		
	}
	
	@Test
	public void test() {
		
		HashMap<String,Object> params = new HashMap<>();
		
		params.put("bcHost", "localhost");
		params.put("bcPort", 8230);
		params.put("serverId", "process-server");
		params.put("groupId", "it.redhat.demo");
		params.put("artifactId", "bpms-rest-task");
		params.put("version", "1.1.0-SNAPSHOT");
		
		kieSession.startProcess("it.redhat.test.unified-managed-deploy", params);
		
	}

}
