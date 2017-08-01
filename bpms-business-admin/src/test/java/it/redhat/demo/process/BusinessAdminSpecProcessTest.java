package it.redhat.demo.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

public class BusinessAdminSpecProcessTest extends JbpmJUnitBaseTestCase {

	private static final String USER_HR1 = "mgallerani";

	private static final String USER_HR2 = "pgarzone";

	private static final String USER_ADMINISTRATOR = "aercoli";

	private static final String THE_MANAGER_FEEDBACK = "the Manager feedback!";

	private static final String THE_HR_FEEDBACK = "the HR feedback!";

	private static final String START_VALUE = "Start Value";

	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";

	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;

	private KieSession kieSession;
	private TaskService taskService;
	private AuditService auditService;

	public BusinessAdminSpecProcessTest() {
		super(true, true);
	}

	@Before
	public void before() {
		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "business-admin-spec.bpmn2");
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
	public void run() {

		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("start", START_VALUE);

		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.business-admin", parameters);
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "Start Process", "HR Task");

		// HR user do task
		parameters.clear();
		parameters.put("feed", THE_HR_FEEDBACK);
		doTask(USER_HR2, parameters);

		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "Manager Task");

		// Manager user do task
		parameters.clear();
		parameters.put("feed", THE_MANAGER_FEEDBACK);
		doTask("gpirola", parameters);

		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "End Process");

		Map<String, List<VariableInstanceLog>> variables = auditService.findVariableInstances(pi.getId()).stream()
				.collect(Collectors.groupingBy(VariableInstanceLog::getVariableId));

		List<VariableInstanceLog> startLogs = variables.get("start");
		assertEquals(1, startLogs.size());
		VariableInstanceLog startLog = startLogs.get(0);

		assertEquals(START_VALUE, startLog.getValue());

		List<VariableInstanceLog> hrLogs = variables.get("hr");
		assertEquals(1, hrLogs.size());
		VariableInstanceLog hrLog = hrLogs.get(0);

		assertEquals(THE_HR_FEEDBACK, hrLog.getValue());

		List<VariableInstanceLog> managerLogs = variables.get("manager");
		assertEquals(1, managerLogs.size());
		VariableInstanceLog managerLog = managerLogs.get(0);

		assertEquals(THE_MANAGER_FEEDBACK, managerLog.getValue());

	}

	private void doTask(String user, HashMap<String, Object> parameters) {

		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(user, "");
		assertEquals(1, tasks.size());
		TaskSummary task = tasks.get(0);

		taskService.claim(task.getId(), user);
		taskService.start(task.getId(), user);
		taskService.complete(task.getId(), user, parameters);

	}

	@Test
	public void test_business_admin() {

		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("start", START_VALUE);

		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.business-admin", parameters);
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "Start Process", "HR Task");

		List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, "");
		assertEquals(1, tasks.size());
		TaskSummary task = tasks.get(0);
		assertEquals(Status.Ready, task.getStatus());

		taskService.claim(task.getId(), USER_HR1);

		tasks = taskService.getTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, "");
		assertEquals(1, tasks.size());
		task = tasks.get(0);
		assertEquals(Status.Reserved, task.getStatus());
		assertEquals(USER_HR1, task.getActualOwnerId());

		taskService.delegate(task.getId(), USER_ADMINISTRATOR, USER_HR2);

		tasks = taskService.getTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, "");
		assertEquals(1, tasks.size());
		task = tasks.get(0);
		assertEquals(Status.Reserved, task.getStatus());
		assertEquals(USER_HR2, task.getActualOwnerId());

		taskService.start(task.getId(), USER_HR2);

		parameters.clear();
		parameters.put("feed", "This is Garzone feedback!");

		taskService.complete(task.getId(), USER_HR2, parameters);

		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "Manager Task");

		// Manager user do task
		parameters.clear();
		parameters.put("feed", THE_MANAGER_FEEDBACK);
		doTask("gpirola", parameters);

		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "End Process");

	}

}
