package it.redhat.demo.v2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

public class SelectionProcessTest extends JbpmJUnitBaseTestCase {
	
	private KieSession kieSession;
	@SuppressWarnings("unused")
	private AuditService auditService;
	private TaskService taskService;
	
	public SelectionProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		createRuntimeManager("it/redhat/demo/v2/selection-process.bpmn2");
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		auditService = runtimeEngine.getAuditService();
		taskService = runtimeEngine.getTaskService();
		
	}
	
	@Test
	public void test_reject() {
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("curriculum", "/curriculum/path/redhat/fercoli");
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.selection-process", params);
		assertProcessInstanceActive(pi.getId());
		
		// find task by process instance id
		List<Long> taskIds = taskService.getTasksByProcessInstanceId(pi.getId());
		assertEquals(1, taskIds.size());
		Long taskId = taskIds.get(0);
		
		Map<String, Object> taskContent = taskService.getTaskContent(taskId);		
		String curriculumParam = (String) taskContent.get("curriculum");
		assertEquals("/curriculum/path/redhat/fercoli", curriculumParam);
		
		// find task by pot owners
		List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("trionfera", "en-UK");
		assertEquals(1, tasksAssignedAsPotentialOwner.size());
		TaskSummary taskSummary = tasksAssignedAsPotentialOwner.get(0);
		assertEquals(Status.Ready, taskSummary.getStatus());
		
		// we will reach the same task instance
		assertEquals(taskSummary.getId(), taskId);
		
		taskService.claim(taskId, "trionfera");
		taskService.start(taskId, "trionfera");
		
		HashMap<String,Object> taskResults = new HashMap<String, Object>();
		taskResults.put("feedback", "KO");
		
		// the HR trionfera reject the candidate
		taskService.complete(taskId, "trionfera", taskResults);
		
		assertNodeTriggered(pi.getId(), "StartProcess", "Human Resource Interview", "Verify HR", "Rejected");
		assertProcessInstanceCompleted(pi.getId());
	}
	
	@Test
	public void test_promoted_rejected() {
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("curriculum", "/curriculum/path/redhat/fercoli");
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.selection-process", params);
		assertProcessInstanceActive(pi.getId());
		
		// find task by pot owners
		List<TaskSummary> hrTasks = taskService.getTasksAssignedAsPotentialOwner("coppellardi", "en-UK");
		assertEquals(1, hrTasks.size());
		TaskSummary hrTask = hrTasks.get(0);
		assertEquals(Status.Ready, hrTask.getStatus());
		
		// the HR coppellardi approve the candidate
		taskService.claim(hrTask.getId(), "coppellardi");
		taskService.start(hrTask.getId(), "coppellardi");
		HashMap<String,Object> coppellardiParams = new HashMap<String, Object>();
		coppellardiParams.put("feedback", "OK");
		taskService.complete(hrTask.getId(), "coppellardi", coppellardiParams);
		
		assertNodeTriggered(pi.getId(), "StartProcess", "Human Resource Interview", "Verify HR", "Manager Interview");
		assertProcessInstanceActive(pi.getId());
		
		List<TaskSummary> managerTasks = taskService.getTasksAssignedAsPotentialOwner("burgalassi", "en-UK");
		assertEquals(1, managerTasks.size());
		TaskSummary managerTask = managerTasks.get(0);
		assertEquals(Status.Ready, managerTask.getStatus());
		
		// the Manager burgalassi reject the candidate
		taskService.claim(managerTask.getId(), "burgalassi");
		taskService.start(managerTask.getId(), "burgalassi");
		HashMap<String,Object> burgalassiParams = new HashMap<String, Object>();
		burgalassiParams.put("feedback", "KO");
		taskService.complete(managerTask.getId(), "burgalassi", burgalassiParams);
		
		assertNodeTriggered(pi.getId(), "Verify Manager", "Reject by Manager");
		assertProcessInstanceCompleted(pi.getId());
		
	}
	
	@Test
	public void test_promoted_promoted() {
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("curriculum", "/curriculum/path/redhat/fercoli");
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.selection-process", params);
		assertProcessInstanceActive(pi.getId());
		
		// find task by pot owners
		List<TaskSummary> hrTasks = taskService.getTasksAssignedAsPotentialOwner("coppellardi", "en-UK");
		assertEquals(1, hrTasks.size());
		TaskSummary hrTask = hrTasks.get(0);
		assertEquals(Status.Ready, hrTask.getStatus());
		
		// the HR coppellardi approve the candidate
		taskService.claim(hrTask.getId(), "coppellardi");
		taskService.start(hrTask.getId(), "coppellardi");
		HashMap<String,Object> coppellardiParams = new HashMap<String, Object>();
		coppellardiParams.put("feedback", "OK");
		taskService.complete(hrTask.getId(), "coppellardi", coppellardiParams);
		
		assertNodeTriggered(pi.getId(), "StartProcess", "Human Resource Interview", "Verify HR", "Manager Interview");
		assertProcessInstanceActive(pi.getId());
		
		List<TaskSummary> managerTasks = taskService.getTasksAssignedAsPotentialOwner("burgalassi", "en-UK");
		assertEquals(1, managerTasks.size());
		TaskSummary managerTask = managerTasks.get(0);
		assertEquals(Status.Ready, managerTask.getStatus());
		
		// the Manager burgalassi reject the candidate
		taskService.claim(managerTask.getId(), "burgalassi");
		taskService.start(managerTask.getId(), "burgalassi");
		HashMap<String,Object> burgalassiParams = new HashMap<String, Object>();
		burgalassiParams.put("feedback", "OK");
		taskService.complete(managerTask.getId(), "burgalassi", burgalassiParams);
		
		assertNodeTriggered(pi.getId(), "Verify Manager", "Hired");
		assertProcessInstanceCompleted(pi.getId());
		
	}

}
