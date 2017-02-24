package it.redhat.demo.test;

import java.util.HashMap;

import it.redhat.demo.stub.MigrationTaskStub;
import it.redhat.demo.stub.RestStub;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

public class MigrationProcessTest extends JbpmJUnitBaseTestCase {
	
	protected final static String PROCESSES_BASE_PATH = "it/redhat/demo/";
	
	protected KieSession kieSession;
	protected RuntimeManager runtimeManager;
	protected RuntimeEngine runtimeEngine;
	
	public MigrationProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {

		System.setProperty("org.kie.server.controller.user", "fabio");
		System.setProperty("org.kie.server.controller.pwd", "fabio$739");

		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "migration.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RestStub());
		kieSession.getWorkItemManager().registerWorkItemHandler("MigrationTask", new MigrationTaskStub());

	}
	
	@After
	public void after() {

		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();

	}
	
	@Test
	public void test() {
		
		HashMap<String,Object> params = new HashMap<>();
		
		params.put("bcHost", "localhost");
		params.put("bcPort", 8230l);
		params.put("oldDeployment", "it.redhat.demo:bpms-selection-process:1.0.0");
		params.put("newDeployment", "it.redhat.demo:bpms-selection-process:1.0.1");
		
		ProcessInstance migrationProcess = kieSession.startProcess("it.redhat.demo.migration", params);
		
		assertProcessInstanceCompleted(migrationProcess.getId());
		assertNodeTriggered(migrationProcess.getId(), "Start Migration", "LoadProcessDefinitions", "Process Definition Instances Migration", "Start Definition Migration", "LoadProcessInstances", "MigrationTask", "End Definition Migration", "End Migration");
		
	}

}
