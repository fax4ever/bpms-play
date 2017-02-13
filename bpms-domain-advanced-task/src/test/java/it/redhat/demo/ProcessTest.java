package it.redhat.demo;

import java.util.HashMap;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.handler.AdvancedWorkItemHandler;

public class ProcessTest extends JbpmJUnitBaseTestCase {
	
	private KieSession kieSession;
	
	public ProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
	
		RuntimeManager runtimeManager = createRuntimeManager("it/redhat/demo/process.bpmn2");
		
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		kieSession.getWorkItemManager().registerWorkItemHandler("AdvancedWorkItemHandler", new AdvancedWorkItemHandler(runtimeManager));
		
	}
	
	@Test
	public void test() {
		
		ProcessInstance instance = kieSession.startProcess("it.redhat.demo.process", new HashMap<>());
		assertProcessInstanceCompleted(instance.getId());
		
	}

}
