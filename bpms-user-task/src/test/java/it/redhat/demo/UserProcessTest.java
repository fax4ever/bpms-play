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
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

public class UserProcessTest extends JbpmJUnitBaseTestCase {

	private static final String VARIABLE_START = "start";
	private static final String VARIABLE_VENDITORE = "venditore";
	private static final String VARIABLE_DIREZIONALE = "direzionale";
	private static final String VARIABLE_MEDICO = "medico";
	private static final String VARIABLE_MANAGER = "manager";
	
	// this users are defined in src/test/resources/usergroups.properties
	private static final String USER_VENDITORE = "pgarzone";
	private static final String USER_DIREZIONALE = "gpirola";
	private static final String USER_MEDICO = "aercoli";
	private static final String USER_MANAGER = "mfulli";

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
		runtimeManager = createRuntimeManager("it/redhat/demo/user-process.bpmn2");
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
		params.put(VARIABLE_START, 1);
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.user-process", params);

		// VERIFY: process instance is active and node triggered
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "Start Process", "Venditore Task");

		// LOOKUP: venditore user task
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(USER_VENDITORE, "");
		assertEquals(1, tasks.size());
		TaskSummary task = tasks.get(0);

		// ACTION: venditore solve user task
		params.clear();
		params.put(VARIABLE_VENDITORE, 2);
		taskService.claim(task.getId(), USER_VENDITORE);
		taskService.start(task.getId(), USER_VENDITORE);
		taskService.complete(task.getId(), USER_VENDITORE, params);

		// VERIFY: process instance is active and node triggered
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "Direzionale Task");

		// LOOKUP: direzionale user task
		tasks = taskService.getTasksAssignedAsPotentialOwner(USER_DIREZIONALE, "");
		assertEquals(1, tasks.size());
		task = tasks.get(0);

		// ACTION: direzionale solve user task
		params.clear();
		params.put(VARIABLE_DIREZIONALE, 3);
		taskService.claim(task.getId(), USER_DIREZIONALE);
		taskService.start(task.getId(), USER_DIREZIONALE);
		taskService.complete(task.getId(), USER_DIREZIONALE, params);

		// VERIFY: process instance is active and node triggered
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "Medico Task");

		// LOOKUP: medico user task
		tasks = taskService.getTasksAssignedAsPotentialOwner(USER_MEDICO, "");
		assertEquals(1, tasks.size());
		task = tasks.get(0);

		// ACTION: medico solve user task
		params.clear();
		params.put(VARIABLE_MEDICO, 4);
		taskService.claim(task.getId(), USER_MEDICO);
		taskService.start(task.getId(), USER_MEDICO);
		taskService.complete(task.getId(), USER_MEDICO, params);

		// VERIFY: process instance is active and node triggered
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "Manager Task");

		// LOOKUP: manager user task
		tasks = taskService.getTasksAssignedAsPotentialOwner(USER_MANAGER, "");
		assertEquals(1, tasks.size());
		task = tasks.get(0);

		// ACTION: manager solve user task
		params.clear();
		params.put(VARIABLE_MANAGER, 5);
		taskService.claim(task.getId(), USER_MANAGER);
		taskService.start(task.getId(), USER_MANAGER);
		taskService.complete(task.getId(), USER_MANAGER, params);

		// VERIFY: process instance is completed and node triggered
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "End Process");
		
		// AUDIT VERIFY the process instance variables [1,2,3,4,5]
		List<? extends VariableInstanceLog> auditVariables = auditService.findVariableInstances(pi.getId());
		
		for (VariableInstanceLog logVariable : auditVariables) {
			
			String value = logVariable.getValue();
			
			String name = logVariable.getVariableId();
			if (VARIABLE_START.equals(name)) {
				
				assertEquals("1", value);
				
			} else if (VARIABLE_VENDITORE.equals(name)) {
				
				assertEquals("2", value);
				
			} else if (VARIABLE_DIREZIONALE.equals(name)) {
				
				assertEquals("3", value);
				
			} else if (VARIABLE_MEDICO.equals(name)) {
				
				assertEquals("4", value);
				
			} else if (VARIABLE_MANAGER.equals(name)) {
				
				assertEquals("5", value);
				
			}
			
		}

	}

}
