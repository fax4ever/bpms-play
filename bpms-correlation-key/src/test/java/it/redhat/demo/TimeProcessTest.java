package it.redhat.demo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.impl.PseudoClockScheduler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.listener.LogProcessEventListener;

public class TimeProcessTest extends JbpmJUnitBaseTestCase {

	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";

	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private AuditService auditService;
	
	public TimeProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		System.setProperty("drools.clockType", "pseudo");
		
		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "time-parent-process.bpmn2", IT_REDHAT_DEMO + "time-sub-process.bpmn2");	
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		auditService = runtimeEngine.getAuditService();
		
		addProcessEventListener(new LogProcessEventListener());
		
	}
	
	@After
	public void after() {
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
		
	}
	
	@Test
	public void test() {
		
		// main process instance
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.time-parent-process");
		
		// sub process
		List<? extends ProcessInstanceLog> subProcessInstances = auditService.findSubProcessInstances(pi.getId());
		assertEquals(1, subProcessInstances.size());
		ProcessInstanceLog subPi = subProcessInstances.get(0);
		
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "CallSubprocess");
		
		assertProcessInstanceActive(subPi.getProcessInstanceId());
		assertNodeTriggered(subPi.getProcessInstanceId(), "StartProcess", "Wait");
		
		// advance pseudo time
		PseudoClockScheduler sessionClock = kieSession.getSessionClock();
		sessionClock.advanceTime(10, TimeUnit.SECONDS);
		
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "EndProcess");
		
		assertProcessInstanceCompleted(subPi.getProcessInstanceId());
		assertNodeTriggered(subPi.getProcessInstanceId(), "EndProcess");
		
	}

}
