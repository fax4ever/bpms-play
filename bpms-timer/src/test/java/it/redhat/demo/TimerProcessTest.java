package it.redhat.demo;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.impl.PseudoClockScheduler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

public class TimerProcessTest extends JbpmJUnitBaseTestCase {
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;

	public TimerProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		System.setProperty("drools.clockType", "pseudo");
		
		runtimeManager = createRuntimeManager("it/redhat/demo/timer-process.bpmn2");	
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
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.timer-process");
		
		PseudoClockScheduler sessionClock = kieSession.getSessionClock();
		sessionClock.advanceTime(10, TimeUnit.SECONDS);
		
		assertNodeTriggered(pi.getId(), "Before Timer", "Timer", "After Timer");
		assertProcessInstanceCompleted(pi.getId());
		
	}
	
}
