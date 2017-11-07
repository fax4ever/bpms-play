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
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;

import it.redhat.demo.listener.LogProcessEventListener;

public class GrandparentTimeProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private AuditService auditService;
	
	private CorrelationKeyFactory factory;
	
	public GrandparentTimeProcessTest() {
		super(true, true);
		factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
	}
	
	@Before
	public void before() {
		
		System.setProperty("drools.clockType", "pseudo");
		LogProcessEventListener listener = new LogProcessEventListener(false);
		addProcessEventListener(listener);
		
		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "time-grandparent-process.bpmn2", IT_REDHAT_DEMO + "time-parent-process.bpmn2", IT_REDHAT_DEMO + "time-sub-process.bpmn2");	
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		auditService = runtimeEngine.getAuditService();

		listener.setRuntimeManager(runtimeManager);
		
	}
	
	@After
	public void after() {
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
		
	}
	
	@Test
	public void test() {
		
		// main process instance
		ProcessInstance grandPi = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.time-grandparent-process", getCorrelationKey(), null);
		
		assertProcessInstanceActive(grandPi.getId());
		assertNodeTriggered(grandPi.getId(), "StartProcess", "Delegate Subprocess");
		
		// parent process
		List<? extends ProcessInstanceLog> parentProcessInstances = auditService.findSubProcessInstances(grandPi.getId());
		assertEquals(1, parentProcessInstances.size());
		ProcessInstanceLog parentPi = parentProcessInstances.get(0);
		
		assertProcessInstanceActive(parentPi.getProcessInstanceId());
		assertNodeTriggered(parentPi.getProcessInstanceId(), "StartProcess", "CallSubprocess");
		
		// sub process
		List<? extends ProcessInstanceLog> subProcessInstances = auditService.findSubProcessInstances(parentPi.getProcessInstanceId());
		assertEquals(1, subProcessInstances.size());
		ProcessInstanceLog subPi = subProcessInstances.get(0);
		
		assertProcessInstanceActive(subPi.getProcessInstanceId());
		assertNodeTriggered(subPi.getProcessInstanceId(), "StartProcess", "Wait");
		
		// advance pseudo time
		PseudoClockScheduler sessionClock = kieSession.getSessionClock();
		sessionClock.advanceTime(10, TimeUnit.SECONDS);
		
		assertProcessInstanceCompleted(subPi.getProcessInstanceId());
		assertNodeTriggered(subPi.getProcessInstanceId(), "EndProcess");
		
		assertProcessInstanceCompleted(parentPi.getProcessInstanceId());
		assertNodeTriggered(parentPi.getProcessInstanceId(), "EndProcess");
		
		assertProcessInstanceCompleted(grandPi.getId());
		assertNodeTriggered(grandPi.getId());
		
	}

	private CorrelationKey getCorrelationKey() {
		return factory.newCorrelationKey("mybusinesskey");
	}

}
