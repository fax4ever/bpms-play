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

import it.redhat.demo.listener.AfterTimerListener;
import it.redhat.demo.listener.ThreadLocalHolder;

public class MultiTimerProcessTest extends JbpmJUnitBaseTestCase {
	
	/* listeners:
		RuleEventListener (org.kie.internal.event.rule) no timer
		KieBaseEventListener (org.kie.api.event.kiebase) no timer
		ProcessEventListener (org.kie.api.event.process) --> OK for filtering after timer
		KnowledgeAgentEventListener (org.kie.internal.event.knowledgeagent) no timer
		AgendaEventListener (org.kie.api.event.rule) no timer
		KieScannerEventListener (org.kie.api.event.kiescanner) no timer
		RuleRuntimeEventListener (org.kie.api.event.rule) no timer
		TaskLifeCycleEventListener (org.kie.api.task) no timer
	*/

	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	
	public MultiTimerProcessTest() {
		super(true, true);
	}

	@Before
	public void before() {
		
		addProcessEventListener(new AfterTimerListener());

		System.setProperty( "drools.clockType", "pseudo" );

		runtimeManager = createRuntimeManager( "it/redhat/demo/multitimer.bpmn2" );
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

	}

	@After
	public void after() {

		runtimeManager.disposeRuntimeEngine( runtimeEngine );
		runtimeManager.close();

	}

	@Test
	public void test() {

		ProcessInstance pi = kieSession.startProcess( "it.redhat.demo.multitimer" );
		
		assertProcessInstanceActive( pi.getId() );
		assertNodeTriggered( pi.getId(), "StartProcess", "User Task 1");

		PseudoClockScheduler sessionClock = kieSession.getSessionClock();
		sessionClock.advanceTime( 4, TimeUnit.SECONDS );
		
		String threadLocalValue = ThreadLocalHolder.get();
		assertEquals( "1", threadLocalValue );
		
		assertProcessInstanceActive( pi.getId() );
		assertNodeTriggered( pi.getId(), "Timer Bou", "Exclusive Gateway 1", "Intermediate Catch Event 2");
		
		sessionClock.advanceTime( 4, TimeUnit.SECONDS );
		
		threadLocalValue = ThreadLocalHolder.get();
		assertEquals( "2", threadLocalValue );
		
		assertProcessInstanceActive( pi.getId() );
		assertNodeTriggered( pi.getId(), "User Task 2");
		
		kieSession.signalEvent( "signal", "my Signal Content" );
		
		assertProcessInstanceCompleted( pi.getId() );
		assertNodeTriggered( pi.getId(), "Signal Bau", "Exclusive Gateway 2", "End Event 3");
		
		// thread local variable because signal boundary not change thread local value!!!
		threadLocalValue = ThreadLocalHolder.get();
		assertEquals( "2", threadLocalValue );
		
	}

}
