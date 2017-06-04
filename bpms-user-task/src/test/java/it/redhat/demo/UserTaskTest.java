package it.redhat.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

public class UserTaskTest extends JbpmJUnitBaseTestCase {

	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private TaskService taskService;
	private AuditService auditService;

	public UserTaskTest() {
		super(true, true);
	}

	@Before
	public void before() {
		runtimeManager = createRuntimeManager("it/redhat/demo/ueer-task.bpmn2");
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

		// ACTION: start process with start parameter
		HashMap<String, Object> params = new HashMap<>();
		
		ArrayList<String> proposalManagers = new ArrayList<String>();
		proposalManagers.add("F1234567");
		proposalManagers.add("F1234569");
		
		String proposalCode = "739";
		
		params.put("proposalCode", proposalCode);
		params.put("proposalManagers", proposalManagers);
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.user-task", params);

		// VERIFY: process instance is active and node triggered
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "ForwardProposal");
		
		// LOOKUP: venditore user task
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("grussell", "");
		assertEquals(1, tasks.size());
		
		TaskSummary taskSummary = tasks.get(0);
		Map<String, Object> taskContent = taskService.getTaskContent(taskSummary.getId());
		String code = (String) taskContent.get("code");
		List<?> managers = (List<?>) taskContent.get("managers");
		
		assertEquals(proposalCode, code);
		assertEquals(proposalManagers, managers);
		
		taskService.start(taskSummary.getId(), "grussell");
		taskService.complete(taskSummary.getId(), "grussell", new HashMap<>());
		
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "EndProcess");

	}

}
