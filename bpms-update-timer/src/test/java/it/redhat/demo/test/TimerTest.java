package it.redhat.demo.test;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.EmptyContext;

import it.redhat.demo.UpgradeCommand;

/**
 * @author Fabio Massimo Ercoli (C) 2017 Red Hat Inc.
 */
public class TimerTest extends JbpmJUnitBaseTestCase {

	private RuntimeEngine engine;
	private KieSession ksession;

	public TimerTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		createRuntimeManager("it/redhat/demo/timer-process.bpmn2");
		engine = getRuntimeEngine(EmptyContext.get());
		ksession = engine.getKieSession();
		
	}
	
	@After
	public void after() {
		
		manager.disposeRuntimeEngine(engine);
		manager.close();
		
	}
	
	@Test
	public void testProcess() throws Exception {
		
		ProcessInstance processInstance = ksession.startProcess("it.redhat.demo.timer-process");
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "StartProcess", "User Task 1");
		
		ksession.execute(new UpgradeCommand(processInstance.getId()));
		Thread.sleep(2000);
		
		assertProcessInstanceCompleted(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Timer1", "Exclusive Gateway", "Intermediate Catch Event", "End Event");
		
		
	}

}
