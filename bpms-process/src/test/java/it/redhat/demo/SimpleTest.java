package it.redhat.demo;

import java.util.HashMap;
import java.util.List;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

public class SimpleTest extends JbpmJUnitBaseTestCase {
	
	private static final String MARY = "john";

	public SimpleTest() {
		super(true, true);
	}
	
	@Test
	public void test() {
		
		createRuntimeManager("it/redhat/demo/simple.bpmn2");
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		TaskService taskService = runtimeEngine.getTaskService();
		KieSession kieSession = runtimeEngine.getKieSession();
		
		Person person = new Person();
		person.setUsername(MARY);
		
		HashMap<String,Object> hashMap = new HashMap<>();
		hashMap.put("person", person);
		
		ProcessInstance startProcess = kieSession.startProcess("it.redhat.demo.simple", hashMap);
		List<Long> tasksByProcessInstanceId = taskService.getTasksByProcessInstanceId(startProcess.getId());
		Long taskId = tasksByProcessInstanceId.get(0);
		Task taskById = taskService.getTaskById(taskId);
		
		PeopleAssignments peopleAssignments = taskById.getPeopleAssignments();
	
		List<OrganizationalEntity> potentialOwners = peopleAssignments.getPotentialOwners();
		Assert.assertEquals(1, potentialOwners.size());
		
		List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner(MARY, "en-UK");
		System.out.println(tasksAssignedAsPotentialOwner);
		Assert.assertEquals(1, tasksAssignedAsPotentialOwner.size());
		
		taskService.start(taskId, MARY);
		taskService.complete(taskId, MARY, new HashMap<>());
		
		
	}

}
