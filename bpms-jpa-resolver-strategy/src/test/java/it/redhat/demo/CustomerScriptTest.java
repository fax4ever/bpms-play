package it.redhat.demo;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * Created by fabio.ercoli@redhat.com on 17/03/17.
 */
public class CustomerScriptTest extends JbpmJUnitBaseTestCase {

	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";

    private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
    
    public CustomerScriptTest() {
        super(true, true);
    }
    
    @Before
    public void before() {

        runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "customer-script.bpmn2");
        runtimeEngine = getRuntimeEngine();
        
        kieSession = runtimeEngine.getKieSession();

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
    	
    }

}
