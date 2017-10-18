package it.redhat.demo.process;

import java.util.List;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.RuntimeExceptionWIH;

public class ExceptionAfterStartErrorsuprocessTest extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;

	private KieSession kieSession;
	private AuditService auditService;
	
	public ExceptionAfterStartErrorsuprocessTest() {
		super(true, true);
	}

	@Before
	public void before() {
		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "exception-after-start-errorsuprocess.bpmn2");
		runtimeEngine = getRuntimeEngine();

		kieSession = runtimeEngine.getKieSession();
		auditService = runtimeEngine.getAuditService();
		kieSession.getWorkItemManager().registerWorkItemHandler("RuntimeException", new RuntimeExceptionWIH());
	}

	@After
	public void after() {

		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();

	}
	
	@Test
	public void test() {
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.exception-after-start-errorsuprocess");
		
		// process instance is aborted
		assertProcessInstanceAborted(pi.getId());
		
		assertNodeTriggered(pi.getId(), "StartProcess", "RuntimeException");
		
		List<? extends ProcessInstanceLog> processInstances = auditService.findProcessInstances();
		assertEquals(1, processInstances.size());
		
		List<? extends VariableInstanceLog> variableInstances = auditService.findVariableInstances(pi.getId());
		
		for (VariableInstanceLog vil : variableInstances) {
		
			if ("exception".equals(vil.getVariableId())) {
				
				assertEquals("java.lang.RuntimeException", vil.getValue());
				
			}
			
		}
		
	}

}
