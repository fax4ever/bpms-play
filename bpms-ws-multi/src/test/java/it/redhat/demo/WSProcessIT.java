package it.redhat.demo;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.ws.Person;

public class WSProcessIT extends JbpmJUnitBaseTestCase {
	
	private static final Logger LOG = LoggerFactory.getLogger(WSProcessIT.class);
	
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
	public void test() throws Exception {
		
		ArrayList<Person> people = new ArrayList<>();
		people.add(new Person());
		
		XMLGregorianCalendar moment = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		
		Object[] request = new Object[3];
		
		request[0] = people;
		request[1] = moment;
		request[2] = 7;
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("request", request);
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.ws-client", params);
		assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "WebService", "EndProcess");
    	
    	List<? extends VariableInstanceLog> variables = auditService.findVariableInstances(pi.getId());
    	
    	for (VariableInstanceLog var : variables) {
    		
    		String value = var.getValue();
    		
    		if ("request".equals(var.getVariableId())) {
    			
    			LOG.info(value);
    			
    		}
    		
    		if ("response".equals(var.getVariableId())) {
    			
    			LOG.info(value);
    			
    		}
    		
    	}
		
	}
	
}
