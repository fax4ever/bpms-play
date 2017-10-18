package it.redhat.demo.process;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import stub.RestSmartStub;

public class TimeoutCatchRemoteTest extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;

	private KieSession kieSession;
	
	public TimeoutCatchRemoteTest() {
		super(true, true);
	}

	@Before
	public void before() {
		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "timeout-catch-remote-service.bpmn2");
		runtimeEngine = getRuntimeEngine();

		kieSession = runtimeEngine.getKieSession();
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RestSmartStub());
	}

	@After
	public void after() {

		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();

	}
	
	@Test
	public void test() {
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.timeout-catch-remote-service");
		
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "Rest1", "RestSlow", "RestCatch", "Rest2", "End Event 2");
		
	}

}
