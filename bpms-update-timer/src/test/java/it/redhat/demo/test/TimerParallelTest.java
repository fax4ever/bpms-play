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
public class TimerParallelTest extends JbpmJUnitBaseTestCase {

	private RuntimeEngine engine;
	private KieSession ksession;

	public TimerParallelTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		createRuntimeManager("it/redhat/demo/timer-parallel.bpmn2");
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
		
		ProcessInstance processInstance = ksession.startProcess("it.redhat.demo.timer-parallel");
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "StartProcess", "Parallel Gateway 1", "User Task 1", "Intermediate Catch Event 1", "Intermediate Catch Event 2", "Sub Process 1");
		
		ksession.execute(new UpgradeCommand(processInstance.getId()));
		Thread.sleep(2000);
		
		assertNodeTriggered(processInstance.getId(), "Timer 1", "Timer 2", "Intermediate Catch Event 1", "Intermediate Catch Event 2");
		assertNodeTriggered(processInstance.getId(), "Exclusive Gateway 1", "Exclusive Gateway 2", "Parallel Gateway 2");
		assertNodeTriggered(processInstance.getId(), "End Event 2");
		assertProcessInstanceCompleted(processInstance.getId());
		
	}

}
