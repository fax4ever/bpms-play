package it.redhat.demo;

import java.util.HashMap;
import java.util.List;

import org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler;
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

public class WSProcessIT extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
	private AuditService auditService;
	
	public WSProcessIT() {
		super(true, true);
	}
	
	@Before
    public void before() {

        runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "ws-client.bpmn2");
        runtimeEngine = getRuntimeEngine();
        
        kieSession = runtimeEngine.getKieSession();
        auditService = runtimeEngine.getAuditService();
        
        kieSession.getWorkItemManager().registerWorkItemHandler("WebService", new WebServiceWorkItemHandler(kieSession));

    }
	
	@After
    public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }
	
	@Test
	public void test() {
		
		String request = "Fabio";
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("request", request);
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.ws-client", params);
		assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "WebService", "EndProcess");
    	
    	List<? extends VariableInstanceLog> variables = auditService.findVariableInstances(pi.getId());
    	assertEquals(2, variables.size());
    	
    	for (VariableInstanceLog var : variables) {
    		
    		String value = var.getValue();
    		
    		if ("request".equals(var.getVariableId())) {
    			
    			assertEquals(request, value);
    			
    		} else if ("response".equals(var.getVariableId())) {
    			
    			assertEquals("ciao Fabio", value);
    			
    		}
    		
    	} 
		
	}
	
}
