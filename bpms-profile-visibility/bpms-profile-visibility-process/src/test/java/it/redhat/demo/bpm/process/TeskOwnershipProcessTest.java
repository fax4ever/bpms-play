package it.redhat.demo.bpm.process;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.bpm.process.wid.ChangeOwnership;

import java.util.HashMap;
import java.util.List;

public class TeskOwnershipProcessTest extends JbpmJUnitBaseTestCase {

	private final static Logger LOG = LoggerFactory.getLogger(TeskOwnershipProcessTest.class);
	
	private static final String PROCESS_FOLDER = "it/redhat/demo/bpm/process/";

    private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
	private TaskService taskService;

	private AuditService auditService;
    
    public TeskOwnershipProcessTest() {
        super(true, true);
    }
    
    @Before
    public void before() {

        runtimeManager = createRuntimeManager(PROCESS_FOLDER + "task-ownership.bpmn2");
        runtimeEngine = getRuntimeEngine();
        
        kieSession = runtimeEngine.getKieSession();
        kieSession.getWorkItemManager().registerWorkItemHandler("ChangeOwnership", new ChangeOwnership());
        
        taskService = runtimeEngine.getTaskService();
        auditService = runtimeEngine.getAuditService();

    }
    
    @After
    public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }
    
    @Test
	public void test_bestcase() {
    	
    	HashMap<String, Object> parameters = new HashMap<>();
    	parameters.put("cip", "A007");
    	parameters.put("agency", "123456");
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.bpm.process.task-ownership", parameters);
		
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "Change Ownership", "Agent Task");
    	
    	List<Long> processInstanceTasks = taskService.getTasksByProcessInstanceId(pi.getId());
    	assertEquals(1, processInstanceTasks.size());
    	Task agentTask = taskService.getTaskById(processInstanceTasks.get(0));
    	logTaskInfo(agentTask);
    	
    	List<OrganizationalEntity> potentialOwners = agentTask.getPeopleAssignments().getPotentialOwners();
		assertEquals(1, potentialOwners.size());
		assertEquals("AXAGEXTAGD_123456", potentialOwners.get(0).getId());
		
		assertEquals(Status.Ready, agentTask.getTaskData().getStatus());
    	
    	taskService.claim(agentTask.getId(), "marco");
    	taskService.start(agentTask.getId(), "marco");
    	taskService.complete(agentTask.getId(), "marco", new HashMap<>());
    	
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "Agent Task 2");
    	
		kieSession.signalEvent("changeCIP", "B003", pi.getId());
    	
    	List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("diego", null);
    	assertEquals(1, tasksAssignedAsPotentialOwner.size());
    	
    	TaskSummary otherTask = tasksAssignedAsPotentialOwner.get(0);
    	logTaskInfo(otherTask);
    	
    	taskService.claim(otherTask.getId(), "diego");
    	taskService.start(otherTask.getId(), "diego");
    	taskService.complete(otherTask.getId(), "diego", new HashMap<>());
    	
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "Checkpoint 2", "Agent Task 3");
    	
		kieSession.signalEvent("changeCIP", "C001", pi.getId());
    	
    	tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("diego", null);
    	assertEquals(1, tasksAssignedAsPotentialOwner.size());
    	
    	otherTask = tasksAssignedAsPotentialOwner.get(0);
    	logTaskInfo(otherTask);
    	
    	taskService.claim(otherTask.getId(), "diego");
    	taskService.start(otherTask.getId(), "diego");
    	taskService.complete(otherTask.getId(), "diego", new HashMap<>());
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "EndProcess");
    	
    	List<? extends VariableInstanceLog> history = auditService.findVariableInstances(pi.getId());
    	LOG.info("variables history: {}", history);
    	
    	int counterChanges = 0;
    	for (VariableInstanceLog log : history) {
    		if (!"owner".equals(log.getVariableId())) {
    			continue;
    		}
    		counterChanges++;
    	}
    	
    	assertEquals(3, counterChanges);
    	
    }
    
    @Test
	public void test_changeAgency() {
    	
    	HashMap<String, Object> parameters = new HashMap<>();
    	parameters.put("cip", "A007");
    	parameters.put("agency", "123456");
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.bpm.process.task-ownership", parameters);
		
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "Change Ownership", "Agent Task");
    	
    	List<Long> processInstanceTasks = taskService.getTasksByProcessInstanceId(pi.getId());
    	assertEquals(1, processInstanceTasks.size());
    	Task agentTask = taskService.getTaskById(processInstanceTasks.get(0));
    	logTaskInfo(agentTask);
    	
    	List<OrganizationalEntity> potentialOwners = agentTask.getPeopleAssignments().getPotentialOwners();
		assertEquals("AXAGEXTAGD_123456", potentialOwners.get(0).getId());
		
		assertEquals(Status.Ready, agentTask.getTaskData().getStatus());
    	
    	taskService.claim(agentTask.getId(), "marco");
    	taskService.start(agentTask.getId(), "marco");
    	taskService.complete(agentTask.getId(), "marco", new HashMap<>());
    	
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "Agent Task 2");
    	
    	List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("diego", null);
    	assertEquals(1, tasksAssignedAsPotentialOwner.size());
    	
    	TaskSummary otherTask = tasksAssignedAsPotentialOwner.get(0);
    	logTaskInfo(otherTask);
    	
    	taskService.claim(otherTask.getId(), "diego");
    	taskService.start(otherTask.getId(), "diego");
    	taskService.complete(otherTask.getId(), "diego", new HashMap<>());
    	
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "Checkpoint 2", "Agent Task 3");
    	
    	tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("diego", null);
    	assertEquals(1, tasksAssignedAsPotentialOwner.size());
    	otherTask = tasksAssignedAsPotentialOwner.get(0);
    	logTaskInfo(otherTask);
    	
    	Long oldTaskId = otherTask.getId();
    	
    	kieSession.signalEvent("changeAgency", "234567", pi.getId());
    	
    	tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("diego", null);
    	assertEquals(0, tasksAssignedAsPotentialOwner.size());
    	tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("dario", null);
    	assertEquals(1, tasksAssignedAsPotentialOwner.size());
    	
    	otherTask = tasksAssignedAsPotentialOwner.get(0);
    	logTaskInfo(otherTask);
    	
    	taskService.claim(otherTask.getId(), "dario");
    	taskService.start(otherTask.getId(), "dario");
    	taskService.complete(otherTask.getId(), "dario", new HashMap<>());
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "EndProcess");
    	
    	List<? extends VariableInstanceLog> history = auditService.findVariableInstances(pi.getId());
    	LOG.info("variables history: {}", history);
    	
    	int counterChanges = 0;
    	for (VariableInstanceLog log : history) {
    		if (!"owner".equals(log.getVariableId())) {
    			continue;
    		}
    		counterChanges++;
    	}
    	
    	Task taskById = taskService.getTaskById(oldTaskId);
    	
    	assertNotEquals(Status.Ready, taskById.getTaskData().getStatus());
    	
    	logTaskInfo(taskById);
    	
    	assertEquals(2, counterChanges);
    	
    }
    
    @Test
	public void test_change_agency_in_early_steps() {
    	
    	HashMap<String, Object> parameters = new HashMap<>();
    	parameters.put("cip", "A007");
    	parameters.put("agency", "123456");
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.bpm.process.task-ownership", parameters);
		
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "Change Ownership", "Agent Task");
    	
    	List<Long> processInstanceTasks = taskService.getTasksByProcessInstanceId(pi.getId());
    	assertEquals(1, processInstanceTasks.size());
    	Task agentTask = taskService.getTaskById(processInstanceTasks.get(0));
    	logTaskInfo(agentTask);
    	
    	List<OrganizationalEntity> potentialOwners = agentTask.getPeopleAssignments().getPotentialOwners();
		assertEquals("AXAGEXTAGD_123456", potentialOwners.get(0).getId());
		
		assertEquals(Status.Ready, agentTask.getTaskData().getStatus());
    	
    	taskService.claim(agentTask.getId(), "marco");
    	taskService.start(agentTask.getId(), "marco");
    	taskService.complete(agentTask.getId(), "marco", new HashMap<>());
    	
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "Agent Task 2");
    	
    	kieSession.signalEvent("changeAgency", "234567", pi.getId());
    	
    	List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("diego", null);
    	assertEquals(1, tasksAssignedAsPotentialOwner.size());
    	
    	TaskSummary otherTask = tasksAssignedAsPotentialOwner.get(0);
    	logTaskInfo(otherTask);
    	
    	taskService.claim(otherTask.getId(), "diego");
    	taskService.start(otherTask.getId(), "diego");
    	taskService.complete(otherTask.getId(), "diego", new HashMap<>());
    	
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "Checkpoint 2", "Agent Task 3");
    	
    	tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("diego", null);
    	assertEquals(1, tasksAssignedAsPotentialOwner.size());
    	
    	otherTask = tasksAssignedAsPotentialOwner.get(0);
    	logTaskInfo(otherTask);
    	
    	taskService.claim(otherTask.getId(), "diego");
    	taskService.start(otherTask.getId(), "diego");
    	taskService.complete(otherTask.getId(), "diego", new HashMap<>());
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "EndProcess");
    	
    	List<? extends VariableInstanceLog> history = auditService.findVariableInstances(pi.getId());
    	LOG.info("variables history: {}", history);
    	
    }

	private void logTaskInfo(TaskSummary task) {
		LOG.info("Task: {} - id: {} - status: {}", task.getName(), task.getId(), task.getStatus());
	}

	private void logTaskInfo(Task task) {
		LOG.info("Task: {} - id: {} - status: {} - potential owners: {}", task.getName(), task.getId(), task.getTaskData().getStatus(), task.getPeopleAssignments().getPotentialOwners());
	}

}
