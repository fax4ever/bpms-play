package it.redhat.demo.process;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.wih.CreatePoliticians;
import it.redhat.demo.wih.LooseRulesStub;

public class RuleClientProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final String PROCESS_FOLDER = "it/redhat/demo/process/";
	
	private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
    
    public RuleClientProcessTest() {
        super(true, true);
    }
    
    @Before
    public void before() {

        runtimeManager = createRuntimeManager(PROCESS_FOLDER + "rule-client.bpmn2");
        runtimeEngine = getRuntimeEngine();
        
        kieSession = runtimeEngine.getKieSession();
        kieSession.getWorkItemManager().registerWorkItemHandler( "FireRules", new LooseRulesStub() );
        kieSession.getWorkItemManager().registerWorkItemHandler( "CreatePoliticians", new CreatePoliticians() );

    }
    
    @After
    public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }
    
    @Test
    public void test() {
    	
    		ProcessInstance pi = kieSession.startProcess( "it.redhat.demo.process.rule-client" );
    		
    		assertProcessInstanceCompleted( pi.getId() );
    		assertNodeTriggered( pi.getId(), "Start Process", "Create Politicians", "Fire Rules", "Log Proliticians", "End Process" );
    		
    	
    }

}
