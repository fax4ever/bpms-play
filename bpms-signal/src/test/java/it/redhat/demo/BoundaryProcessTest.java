package it.redhat.demo;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

public class BoundaryProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
	private AuditService auditService;
	
	public BoundaryProcessTest() {
		super(true, true);
	}
	
	@Before
    public void before() {

        runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "boundary.bpmn2");
        runtimeEngine = getRuntimeEngine();
        
        kieSession = runtimeEngine.getKieSession();
        auditService = runtimeEngine.getAuditService();

    }
	
	@After
    public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }
	
	@Test
	public void test() {
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.boundary");
		assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "User Task 1");
    	
    	kieSession.signalEvent("mysignal", "This is the event content!", pi.getId());
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "End Event 2");
    	
    	ProcessInstanceLog piLog = auditService.findProcessInstance(pi.getId());
    	
    	assertNotNull(piLog);
		
	}
	
}
