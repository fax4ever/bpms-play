package it.redhat.demo.test;

import it.redhat.demo.customtask.ChooseDeployStrategy;
import it.redhat.demo.customtask.CreateContainerSpec;
import it.redhat.demo.customtask.InputValidator;
import it.redhat.demo.customtask.WaitTask;
import it.redhat.demo.stub.RestStub;
import it.redhat.demo.stub.VerifyServerStub;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import java.util.HashMap;

public class UnifiedManagedDeployTest extends JbpmJUnitBaseTestCase {

	protected final static String PROCESSES_BASE_PATH = "it/redhat/demo/";

	protected KieSession kieSession;
	protected RuntimeManager runtimeManager;
	protected RuntimeEngine runtimeEngine;

	public UnifiedManagedDeployTest() {
		super(true, true);
	}

	@Before
	public void before() {

		System.setProperty("org.kie.server.controller.user", "fabio");
		System.setProperty("org.kie.server.controller.pwd", "fabio$739");
		System.setProperty("org.kie.server.user", "fabio");
		System.setProperty("org.kie.server.pwd", "fabio$739");

		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "create-container.bpmn2",
				PROCESSES_BASE_PATH + "remove-container.bpmn2", PROCESSES_BASE_PATH + "migration.bpmn2",
				PROCESSES_BASE_PATH + "unified-managed-deploy.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RestStub());
		kieSession.getWorkItemManager().registerWorkItemHandler("ProcessServerRest", new VerifyServerStub());
		kieSession.getWorkItemManager().registerWorkItemHandler("ChooseDeployStrategy", new ChooseDeployStrategy());
		kieSession.getWorkItemManager().registerWorkItemHandler("CreateContainerSpec", new CreateContainerSpec());
		kieSession.getWorkItemManager().registerWorkItemHandler("InputValidator", new InputValidator());
		kieSession.getWorkItemManager().registerWorkItemHandler("WaitTask", new WaitTask());

	}

	@After
	public void after() {

		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();

	}

	@Test
	public void test() {

		HashMap<String, Object> params = new HashMap<>();

		params.put("bcHost", "localhost");
		params.put("bcPort", "8230s");
		params.put("serverId", "process-server");
		params.put("groupId", "it.redhat.demo");
		params.put("artifactId", "bpms-rest-task");
		params.put("version", "1.1.0-SNAPSHOTs");

		testProcess(params);

	}

	protected void testProcess(HashMap<String, Object> params) {
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.unified-managed-deploy", params);

		assertProcessInstanceCompleted(pi.getId());
	}

}
