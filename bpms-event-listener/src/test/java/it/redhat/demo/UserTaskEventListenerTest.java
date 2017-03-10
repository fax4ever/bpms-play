package it.redhat.demo;

import java.util.HashMap;
import java.util.List;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

import it.redhat.demo.listener.MyTaskEventListener;

public class UserTaskEventListenerTest extends JbpmJUnitBaseTestCase {
	
	private static final String COLTRINARI = "coltrinari";
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private TaskService taskService;
	
	public UserTaskEventListenerTest() {
		super(true, true);
	}

	@Before
	public void before() {
		
		addTaskEventListener(new MyTaskEventListener());
		runtimeManager = createRuntimeManager("it/redhat/demo/user-task-process.bpmn2");	
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		taskService = runtimeEngine.getTaskService();
		
	}
	
	@After
	public void after() {
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
		
	}
	
	@Test
	public void test() {
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.user-task-process");
		
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "UserTask");
		
		List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner(COLTRINARI, "");
		assertEquals(1, tasksAssignedAsPotentialOwner.size());
		
		TaskSummary taskSummary = tasksAssignedAsPotentialOwner.get(0);
		taskService.claim(taskSummary.getId(), COLTRINARI);
		taskService.start(taskSummary.getId(), COLTRINARI);
		taskService.complete(taskSummary.getId(), COLTRINARI, new HashMap<>());
		
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "EndProcess");
		
	}

}
