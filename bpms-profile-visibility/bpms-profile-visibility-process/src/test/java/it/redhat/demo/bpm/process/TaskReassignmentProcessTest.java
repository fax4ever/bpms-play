package it.redhat.demo.bpm.process;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;

public class TaskReassignmentProcessTest extends JbpmJUnitBaseTestCase {

	private final static Logger LOG = LoggerFactory.getLogger(TaskReassignmentProcessTest.class);

	private static final String PROCESS_FOLDER = "it/redhat/demo/bpm/process/";

	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private TaskService taskService;

	public TaskReassignmentProcessTest() {
		super(true, true);
	}

	@Before
	public void before() {

		runtimeManager = createRuntimeManager(PROCESS_FOLDER + "task-reassignment.bpmn2");
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
		
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("agency", "123456");
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.bpm.process.task-reassignment", parameters);
		
		assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "Agent Task");
    	
    	List<Long> processInstanceTasks = taskService.getTasksByProcessInstanceId(pi.getId());
    	assertEquals(1, processInstanceTasks.size());
    	Task agentTask = taskService.getTaskById(processInstanceTasks.get(0));
    	logTaskInfo(agentTask);
    	
    	List<OrganizationalEntity> potentialOwners = agentTask.getPeopleAssignments().getPotentialOwners();
		assertEquals(1, potentialOwners.size());
		assertEquals("INSURANCE_AGENT_ROLE_123456", potentialOwners.get(0).getId());
		
		assertEquals(Status.Ready, agentTask.getTaskData().getStatus());
    	
    	taskService.claim(agentTask.getId(), "marco");
    	taskService.start(agentTask.getId(), "marco");
    	taskService.complete(agentTask.getId(), "marco", new HashMap<>());
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "EndProcess");
		
	}
	
	@Test
	public void test_reassignment() {
		
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("agency", "123456");
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.bpm.process.task-reassignment", parameters);
		
		assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "Agent Task");
    	
    	List<Long> processInstanceTasks = taskService.getTasksByProcessInstanceId(pi.getId());
    	assertEquals(1, processInstanceTasks.size());
    	Task agentTask = taskService.getTaskById(processInstanceTasks.get(0));
    	logTaskInfo(agentTask);
    	
    	List<OrganizationalEntity> potentialOwners = agentTask.getPeopleAssignments().getPotentialOwners();
		assertEquals(1, potentialOwners.size());
		assertEquals("INSURANCE_AGENT_ROLE_123456", potentialOwners.get(0).getId());
		
		assertEquals(Status.Ready, agentTask.getTaskData().getStatus());
    	
    	taskService.claim(agentTask.getId(), "marco");
    	taskService.start(agentTask.getId(), "marco");
    
		try {
			Thread.sleep(3000l);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		taskService.getTasksByProcessInstanceId(pi.getId());
    	assertEquals(1, processInstanceTasks.size());
    	agentTask = taskService.getTaskById(processInstanceTasks.get(0));
    	logTaskInfo(agentTask);
    	
    	taskService.claim(agentTask.getId(), "diego");
    	taskService.start(agentTask.getId(), "diego");
    	taskService.complete(agentTask.getId(), "diego", new HashMap<>());
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "EndProcess");
		
	}
	
	private void logTaskInfo(Task task) {
		LOG.info("Task: {} - id: {} - status: {} - potential owners: {}", task.getName(), task.getId(), task.getTaskData().getStatus(), task.getPeopleAssignments().getPotentialOwners());
	}

}
