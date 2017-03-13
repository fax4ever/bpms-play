package it.redhat.demo;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.listener.LogProcessEventListener;

public class ScriptProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	
	public ScriptProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		addProcessEventListener(new LogProcessEventListener());
		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "script-parent-process.bpmn2", IT_REDHAT_DEMO + "script-sub-process.bpmn2");	
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
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.script-parent-process");
		
		assertProcessInstanceCompleted(pi.getId());
		
	}

}
