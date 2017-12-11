package it.workshop;

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

import it.workshop.model.SoggettoDebitore;

public class RecuperoTest extends JbpmJUnitBaseTestCase {
	
	private static final String PROCESS_FOLDER = "it/workshop/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private TaskService taskService;
	
	public RecuperoTest() {
		super(true, true);
	}
	
	@Before
	public void before() {

		runtimeManager = createRuntimeManager(PROCESS_FOLDER + "recupero.bpmn2");
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
	public void test_autonomo() {
		
		HashMap<String, Object> paramters = new HashMap<>();
		paramters.put("idPratica", "ID1234567");
		paramters.put("soggettoDebitore", new SoggettoDebitore("1234", "Gianalessandro", "Marrone", 300));
		
		ProcessInstance processInstance = kieSession.startProcess("it.workshop.recupero", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Start Recupero", "Restart", "Abbandono / Recupero");
		
		List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("michele", null);
		
		assertEquals(1, taskList.size());
		TaskSummary task = taskList.get(0);
		
		taskService.claim(task.getId(), "michele");
		taskService.start(task.getId(), "michele");
		
		paramters.clear();
		paramters.put("recupero", true);
		
		taskService.complete(task.getId(), "michele", paramters);
		
		assertProcessInstanceCompleted(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Verifica Autonomia", "End Autonomo");
		
	}
	
	@Test
	public void test_approvato() {
		
		HashMap<String, Object> paramters = new HashMap<>();
		paramters.put("idPratica", "ID1234567");
		paramters.put("soggettoDebitore", new SoggettoDebitore("1234", "Gianalessandro", "Marrone", 700));
		
		ProcessInstance processInstance = kieSession.startProcess("it.workshop.recupero", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Start Recupero", "Restart", "Abbandono / Recupero");
		
		List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("michele", null);
		assertEquals(1, taskList.size());
		TaskSummary task = taskList.get(0);
		
		taskService.claim(task.getId(), "michele");
		taskService.start(task.getId(), "michele");
		
		paramters.clear();
		paramters.put("recupero", true);
		
		taskService.complete(task.getId(), "michele", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Verifica Autonomia", "Approvazione");
		
		taskList = taskService.getTasksAssignedAsPotentialOwner("dario", null);
		assertEquals(1, taskList.size());
		task = taskList.get(0);
		
		taskService.claim(task.getId(), "dario");
		taskService.start(task.getId(), "dario");
		
		paramters.clear();
		paramters.put("approvazione", true);
		
		taskService.complete(task.getId(), "dario", paramters);
		
		assertProcessInstanceCompleted(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Verifica Approvazione", "End Approvato");
		
	}
	
	@Test
	public void test_non_approvato() {
		
		HashMap<String, Object> paramters = new HashMap<>();
		paramters.put("idPratica", "ID1234567");
		paramters.put("soggettoDebitore", new SoggettoDebitore("1234", "Gianalessandro", "Marrone", 300));
		
		ProcessInstance processInstance = kieSession.startProcess("it.workshop.recupero", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Start Recupero", "Restart", "Abbandono / Recupero");
		
		List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("michele", null);
		assertEquals(1, taskList.size());
		TaskSummary task = taskList.get(0);
		
		taskService.claim(task.getId(), "michele");
		taskService.start(task.getId(), "michele");
		
		paramters.clear();
		paramters.put("recupero", false);
		
		taskService.complete(task.getId(), "michele", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Verifica Autonomia", "Approvazione");
		
		taskList = taskService.getTasksAssignedAsPotentialOwner("dario", null);
		assertEquals(1, taskList.size());
		task = taskList.get(0);
		
		taskService.claim(task.getId(), "dario");
		taskService.start(task.getId(), "dario");
		
		paramters.clear();
		paramters.put("approvazione", false);
		
		taskService.complete(task.getId(), "dario", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Verifica Approvazione");
		
	}

}
