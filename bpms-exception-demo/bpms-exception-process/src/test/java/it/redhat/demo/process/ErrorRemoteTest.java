package it.redhat.demo.process;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;

import stub.RestSmartStub;

public class ErrorRemoteTest extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;

	private KieSession kieSession;
	
	public ErrorRemoteTest() {
		super(true, true);
	}

	@Before
	public void before() {
		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "error-remote-service.bpmn2");
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
		
		Throwable cause = null;
		
		try {
		
			kieSession.startProcess("it.redhat.demo.error-remote-service");
			
		} catch (WorkflowRuntimeException e) {
			
			cause = e.getCause();
			
		}
		
		assertNotNull(cause);
		assertEquals(WorkflowRuntimeException.class, cause.getClass());
		
	}

}
