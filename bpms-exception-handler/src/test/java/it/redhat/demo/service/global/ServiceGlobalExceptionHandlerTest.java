package it.redhat.demo.service.global;

import java.util.HashMap;

import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.service.ExceptionService;

public class ServiceGlobalExceptionHandlerTest extends JbpmJUnitBaseTestCase {
	
	protected final static String PROCESSES_BASE_PATH = "it/redhat/demo/";
	
	protected KieSession kieSession;
	protected RuntimeManager runtimeManager;
	protected RuntimeEngine runtimeEngine;
	
	public ServiceGlobalExceptionHandlerTest() {
		super(true, true);
	}
	
	@Before
	public void before() {

		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "service-global-exception-handler.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		
		String eventType = "Error-code";
		SignallingTaskHandlerDecorator signallingTaskWrapper = new SignallingTaskHandlerDecorator(ServiceTaskHandler.class, eventType);
		signallingTaskWrapper.setWorkItemExceptionParameterName(ExceptionService.exceptionParameterName);
		kieSession.getWorkItemManager().registerWorkItemHandler("Service Task", signallingTaskWrapper);

	}
	
	@After
	public void after() {

		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();

	}
	
	@Test
	public void test() {
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("serviceInputItem", "Input to Original Service");
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.service-global-exception-handler", params);
		
		assertProcessInstanceAborted(pi.getId());
		
	}

}
