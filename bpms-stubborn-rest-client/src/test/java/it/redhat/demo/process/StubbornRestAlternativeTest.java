package it.redhat.demo.process;

import java.util.HashMap;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.model.Command;
import it.redhat.demo.wih.IncreaseAttempts;
import it.redhat.demo.wih.InitTask;

public class StubbornRestAlternativeTest extends JbpmJUnitBaseTestCase {
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	
	public StubbornRestAlternativeTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		System.setProperty("drools.clockType", "pseudo");
		
		runtimeManager = createRuntimeManager("it/redhat/demo/stubborn-rest-alternative.bpmn2");	
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
	
		kieSession.getWorkItemManager().registerWorkItemHandler("InitTask", new InitTask());
		kieSession.getWorkItemManager().registerWorkItemHandler("IncreaseAttempts", new IncreaseAttempts());
		
	}
	
	@After
	public void after() {
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
		
	}
	
	@Test
	public void test_happyPath() {
	
		// register happy path stub
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new HappyRestStub());
		
		Command command = new Command();
		command.setName("Fabio M.");
		command.setValue(739);
		command.setOption(false);
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("url", "http://localhost:8080/eap6-rest/command");
		params.put("method", "POST");
		params.put("content", command);
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.stubborn-rest-alternative", params);
		
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "InitTask", "Try Rest Call", "Rest", "RestException", "IncreaseAttempts", "Time / User", "Wait Time");
		
	}

}
