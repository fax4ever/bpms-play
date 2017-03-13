package it.redhat.demo;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;

import it.redhat.demo.listener.LogProcessEventListener;
import it.redhat.demo.listener.LogTaskEventListener;

public class UserProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private TaskService taskService;
	private AuditService auditService;
	
	public UserProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		addProcessEventListener(new LogProcessEventListener());
		addTaskEventListener(new LogTaskEventListener());
		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "user-parent-process.bpmn2", IT_REDHAT_DEMO + "user-sub-process.bpmn2");	
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		taskService = runtimeEngine.getTaskService();
		auditService = runtimeEngine.getAuditService();
		
	}
	
	@After
	public void after() {
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
		
	}
	
	@Test
	public void test() {
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.user-parent-process");
		
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "CallSubprocess");
		
	}

}
