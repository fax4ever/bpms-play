package it.redhat.demo;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

public class ProcessRuleTest extends JbpmJUnitBaseTestCase {
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;

	public ProcessRuleTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		runtimeManager = createRuntimeManager("it/redhat/demo/process-rule.bpmn2");
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
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.process-rule");
		
		assertProcessInstanceCompleted(pi.getId());
	}
	
	
}
