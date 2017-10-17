package it.redhat.demo.process;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;

import it.redhat.demo.RuntimeExceptionWIH;

public class ExceptionAfterStartTest extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;

	private KieSession kieSession;
	
	public ExceptionAfterStartTest() {
		super(true, true);
	}

	@Before
	public void before() {
		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "exception-after-start.bpmn2");
		runtimeEngine = getRuntimeEngine();

		kieSession = runtimeEngine.getKieSession();
		kieSession.getWorkItemManager().registerWorkItemHandler("RuntimeException", new RuntimeExceptionWIH());
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
		
			kieSession.startProcess("it.redhat.demo.exception-after-start");
			
		} catch (WorkflowRuntimeException e) {
			
			cause = e.getCause();
			
		}
		
		assertNotNull(cause);
		assertEquals(RuntimeException.class, cause.getClass());
		
	}

}
