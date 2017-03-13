package it.redhat.demo;

import java.util.List;

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

public class ScriptProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private AuditService auditService;
	
	public ScriptProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "script-parent-process.bpmn2", IT_REDHAT_DEMO + "script-sub-process.bpmn2");	
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
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.script-parent-process");
		
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "CallSubprocess", "EndProcess");
		
		List<? extends ProcessInstanceLog> subProcessInstances = auditService.findSubProcessInstances(pi.getId());
		assertEquals(1, subProcessInstances.size());
		ProcessInstanceLog subPi = subProcessInstances.get(0);
		
		assertProcessInstanceCompleted(subPi.getProcessInstanceId());
		assertNodeTriggered(subPi.getProcessInstanceId(), "StartProcess", "ScriptTask", "EndProcess");
		
	}

}
