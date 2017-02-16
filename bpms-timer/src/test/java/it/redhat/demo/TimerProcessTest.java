package it.redhat.demo;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;

public class TimerProcessTest extends JbpmJUnitBaseTestCase {
	
	private KieSession kieSession;

	public TimerProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		createRuntimeManager("it/redhat/demo/timer-process.bpmn2");
		
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		
	}
	
	@Test
	public void test() {
		
		kieSession.startProcess("it.redhat.demo.timer-process");
		
	}
	
}
