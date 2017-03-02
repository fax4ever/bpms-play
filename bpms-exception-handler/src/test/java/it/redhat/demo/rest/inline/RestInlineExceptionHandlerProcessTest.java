package it.redhat.demo.rest.inline;

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
import it.redhat.demo.util.WIRuntimeExceptionThrowerWid;

public class RestInlineExceptionHandlerProcessTest extends JbpmJUnitBaseTestCase {
	
	protected final static String PROCESSES_BASE_PATH = "it/redhat/demo/";
	
	protected KieSession kieSession;
	protected RuntimeManager runtimeManager;
	protected RuntimeEngine runtimeEngine;
	
	public RestInlineExceptionHandlerProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {

		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "rest-inline-exception-handler.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new WIRuntimeExceptionThrowerWid());

	}
	
	@After
	public void after() {

		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();

	}
	
	@Test
	public void test() {
		
		HashMap<String, Object> parameters = new HashMap<>();
		
		Command command = new Command();
		command.setName("Fabio M.");
		command.setValue(739);
		command.setOption(false);
		
		parameters.put("command", command);
		
		ProcessInstance pinstance = kieSession.startProcess("it.redhat.demo.rest-inline-exception-handler", parameters);
		assertProcessInstanceCompleted(pinstance.getId());
		
	}

}
