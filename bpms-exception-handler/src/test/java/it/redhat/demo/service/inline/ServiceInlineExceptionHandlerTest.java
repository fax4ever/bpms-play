package it.redhat.demo.service.inline;

import java.util.HashMap;

import org.jbpm.process.workitem.bpmn2.ServiceTaskHandler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

public class ServiceInlineExceptionHandlerTest extends JbpmJUnitBaseTestCase {
	
protected final static String PROCESSES_BASE_PATH = "it/redhat/demo/";
	
	protected KieSession kieSession;
	protected RuntimeManager runtimeManager;
	protected RuntimeEngine runtimeEngine;
	
	public ServiceInlineExceptionHandlerTest() {
		super(true, true);
	}
	
	@Before
	public void before() {

		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "service-inline-exception-handler.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

		kieSession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(kieSession, this.getClass().getClassLoader()));

	}
	
	@After
	public void after() {

		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();

	}
	
	@Test
	public void test() {
		
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("input", "this is the input");
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.service-inline-exception-handler", parameters);
		
		assertProcessInstanceCompleted(pi.getId());
		
	}

}
