package it.redhat.demo;

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

public class SignalProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
	private AuditService auditService;
	
	public SignalProcessTest() {
		super(true, true);
	}
	
	@Before
    public void before() {

        runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "signal.bpmn2");
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
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.signal");
		assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "IntermediateCatchEvent");
    	
    	kieSession.signalEvent("mysignal", "This is the event content!", pi.getId());
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "EndProcess");
    	
    	ProcessInstanceLog piLog = auditService.findProcessInstance(pi.getId());
    	
    	assertNotNull(piLog);
    	
    	List<? extends VariableInstanceLog> variables = auditService.findVariableInstances(pi.getId());
    	assertEquals(1, variables.size());
    	VariableInstanceLog variableInstanceLog = variables.get(0);
    	
    	assertEquals("This is the event content!", variableInstanceLog.getValue());
		
	}
	
}
