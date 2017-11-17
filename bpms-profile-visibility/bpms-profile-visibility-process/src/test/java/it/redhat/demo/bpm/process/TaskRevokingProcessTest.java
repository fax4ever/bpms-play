package it.redhat.demo.bpm.process;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.bpm.process.command.AddPeopleAssignmentsCommand;
import it.redhat.demo.bpm.process.wid.RevokingTask;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;

public class TaskRevokingProcessTest extends JbpmJUnitBaseTestCase {

	private final static Logger LOG = LoggerFactory.getLogger(TaskRevokingProcessTest.class);

	private static final String PROCESS_FOLDER = "it/redhat/demo/bpm/process/";

	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private TaskService taskService;

	public TaskRevokingProcessTest() {
		super(true, true);
	}

	@Before
	public void before() {

		runtimeManager = createRuntimeManager(PROCESS_FOLDER + "task-revoking.bpmn2");
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		taskService = runtimeEngine.getTaskService();
		
		kieSession.getWorkItemManager().registerWorkItemHandler("RevokingTask", new RevokingTask(runtimeManager));

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
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.bpm.process.task-revoking", parameters);
		
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
	public void test_command() {

		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("agency", "123456");

		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.bpm.process.task-revoking", parameters);

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

		Group group = TaskModelProvider.getFactory().newGroup();
		((InternalOrganizationalEntity) group).setId( "INSURANCE_AGENT_ROLE_234567");

		OrganizationalEntity[] entities = new OrganizationalEntity[1];
		entities[0] = group;

		taskService.execute( new AddPeopleAssignmentsCommand( "zuzu", agentTask.getId(), AddPeopleAssignmentsCommand.POT_OWNER, entities, true ) );

		processInstanceTasks = taskService.getTasksByProcessInstanceId(pi.getId());
		assertEquals(1, processInstanceTasks.size());
		agentTask = taskService.getTaskById(processInstanceTasks.get(0));
		logTaskInfo(agentTask);

		potentialOwners = agentTask.getPeopleAssignments().getPotentialOwners();
		assertEquals(1, potentialOwners.size());
		assertEquals("INSURANCE_AGENT_ROLE_234567", potentialOwners.get(0).getId());

		taskService.claim(agentTask.getId(), "samuele");
		taskService.start(agentTask.getId(), "samuele");
		taskService.complete(agentTask.getId(), "samuele", new HashMap<>());

		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "EndProcess");

	}
	
	@Test
	public void test_revoking() {
		
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("agency", "123456");
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.bpm.process.task-revoking", parameters);
		
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
	
		kieSession.signalEvent("changeAgency", "234567");
		
		processInstanceTasks = taskService.getTasksByProcessInstanceId(pi.getId());
    	assertEquals(1, processInstanceTasks.size());
    	agentTask = taskService.getTaskById(processInstanceTasks.get(0));
    	logTaskInfo(agentTask);

		potentialOwners = agentTask.getPeopleAssignments().getPotentialOwners();
		assertEquals(1, potentialOwners.size());
		assertEquals("INSURANCE_AGENT_ROLE_234567", potentialOwners.get(0).getId());

		taskService.claim(agentTask.getId(), "samuele");
		taskService.start(agentTask.getId(), "samuele");
		taskService.complete(agentTask.getId(), "samuele", new HashMap<>());
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "EndProcess");
		
	}
	
	private void logTaskInfo(Task task) {
		LOG.info("Task: {} - id: {} - status: {} - potential owners: {}", task.getName(), task.getId(), task.getTaskData().getStatus(), task.getPeopleAssignments().getPotentialOwners());
	}

}
