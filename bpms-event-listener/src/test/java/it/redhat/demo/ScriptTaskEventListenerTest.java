package it.redhat.demo;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.listener.MyProcessEventListener;

public class ScriptTaskEventListenerTest extends JbpmJUnitBaseTestCase {
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	
	public ScriptTaskEventListenerTest() {
		super(true, true);
	}

	@Before
	public void before() {
		
		addProcessEventListener(new MyProcessEventListener());
		runtimeManager = createRuntimeManager("it/redhat/demo/script-task-process.bpmn2");	
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
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.script-task-process");
		
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "ScriptTask", "EndProcess");
		
	}

}
