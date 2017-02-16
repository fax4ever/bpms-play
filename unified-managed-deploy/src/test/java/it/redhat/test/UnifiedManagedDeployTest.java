package it.redhat.test;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.impl.PseudoClockScheduler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.customtask.ChooseDeployStrategy;
import it.redhat.demo.customtask.CreateContainerSpec;
import it.redhat.demo.customtask.InputValidator;
import it.redhat.test.stub.RestStub;
import it.redhat.test.stub.VerifyServerStub;

public class UnifiedManagedDeployTest extends JbpmJUnitBaseTestCase {
	
	protected KieSession kieSession;
	protected RuntimeManager runtimeManager;
	protected RuntimeEngine runtimeEngine;
	
	public UnifiedManagedDeployTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		//Enable the PseudoClock using the following system property.
		System.setProperty("drools.clockType", "pseudo");
		
		runtimeManager = createRuntimeManager("it/redhat/test/create-container.bpmn2", "it/redhat/test/update-container.bpmn2", "it/redhat/test/migration.bpmn2", "it/redhat/test/unified-managed-deploy.bpmn2");
		
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RestStub());
		kieSession.getWorkItemManager().registerWorkItemHandler("ProcessServerRest", new VerifyServerStub());
		kieSession.getWorkItemManager().registerWorkItemHandler("ChooseDeployStrategy", new ChooseDeployStrategy());
		kieSession.getWorkItemManager().registerWorkItemHandler("CreateContainerSpec", new CreateContainerSpec());
		kieSession.getWorkItemManager().registerWorkItemHandler("InputValidator", new InputValidator());
		
	}
	
	@After
	public void after() {
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();

	}
	
	@Test
	public void test_bcPortIntValue() {
		
		HashMap<String,Object> params = new HashMap<>();
		
		params.put("bcHost", "localhost");
		params.put("bcPortInt", 8230);
		params.put("serverId", "process-server");
		params.put("groupId", "it.redhat.demo");
		params.put("artifactId", "bpms-rest-task");
		params.put("version", "1.1.0-SNAPSHOT");
		
		testProcess(params);
		
	}

	protected void testProcess(HashMap<String, Object> params) {
		ProcessInstance pi = kieSession.startProcess("it.redhat.test.unified-managed-deploy", params);
		
		PseudoClockScheduler sessionClock = kieSession.getSessionClock();
		sessionClock.advanceTime(10, TimeUnit.SECONDS);
		
		assertProcessInstanceCompleted(pi.getId());
	}

}
