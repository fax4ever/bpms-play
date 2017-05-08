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
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.entity.Customer;

/**
 * Created by fabio.ercoli@redhat.com on 17/03/17.
 */
public class CustomerScriptTest extends JbpmJUnitBaseTestCase {

	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";

    private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
	private AuditService auditService;
    
    public CustomerScriptTest() {
        super(true, true);
    }
    
    @Before
    public void before() {

        runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "customer-script.bpmn2");
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
    	
    	ProcessInstance pi = kieSession.startProcess("it.redhat.demo.customer-script");
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "SaveCustomer", "EndProcess");
    	
    	List<? extends VariableInstanceLog> logs = auditService.findVariableInstances(pi.getId());
    	assertEquals(1, logs.size());
    	
    	VariableInstanceLog log = logs.get(0);
    	String value = log.getValue();
    	
    	assertEquals(new Customer("Fabio M.", true).toString(), value);
    	
    }

}
