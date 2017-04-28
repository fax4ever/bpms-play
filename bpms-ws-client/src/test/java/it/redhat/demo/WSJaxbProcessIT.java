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

import it.redhat.demo.wid.ManageResponseWid;
import it.redhat.demo.ws.ExpenseRequest;

public class WSJaxbProcessIT extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
	private AuditService auditService;
	
	public WSJaxbProcessIT() {
		super(true, true);
	}
	
	@Before
    public void before() {

        runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "ws-jaxb-client.bpmn2");
        runtimeEngine = getRuntimeEngine();
        
        kieSession = runtimeEngine.getKieSession();
        auditService = runtimeEngine.getAuditService();
        
        kieSession.getWorkItemManager().registerWorkItemHandler("WebService", new WebServiceWorkItemHandler(kieSession));
        kieSession.getWorkItemManager().registerWorkItemHandler("ManageResponse", new ManageResponseWid());

    }
	
	@After
    public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }
	
	@Test
	public void test() {
		
		ExpenseRequest request = new ExpenseRequest();
		request.setEmployeeCode("FERCOLI731");
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("request", request);
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.ws-client", params);
		assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "WebService", "ManageResponse", "EndProcess");
    	
    	List<? extends VariableInstanceLog> variables = auditService.findVariableInstances(pi.getId());
    	assertEquals(3, variables.size());
		
	}
	
}
