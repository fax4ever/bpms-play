package it.workshop;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.impl.PseudoClockScheduler;
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

import it.workshop.model.Rata;
import it.workshop.model.SoggettoDebitore;
import it.workshop.stub.CaricaRataStub;

public class IncassoTest extends JbpmJUnitBaseTestCase {
	
	private static final String PROCESS_FOLDER = "it/workshop/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private TaskService taskService;
	
	public IncassoTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		System.setProperty("drools.clockType", "pseudo");

		runtimeManager = createRuntimeManager(PROCESS_FOLDER + "incasso.bpmn2");
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
		
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new CaricaRataStub(true, 777));
		
		HashMap<String, Object> paramters = new HashMap<>();
		paramters.put("idPratica", "ID1234567");
		paramters.put("soggettoDebitore", new SoggettoDebitore("1234", "Gianalessandro", "Marrone", 300));
		
		ProcessInstance processInstance = kieSession.startProcess("it.workshop.incasso", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Start Incasso", "Repeat Carica", "Carica Rata", "Repeat Incasso", "Incasso Rata");
		
		List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("alessandra", null);
		assertEquals(1, taskList.size());
		TaskSummary task = taskList.get(0);
		
		taskService.claim(task.getId(), "alessandra");
		taskService.start(task.getId(), "alessandra");
		taskService.complete(task.getId(), "alessandra", new HashMap<>());
		
		assertProcessInstanceCompleted(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Verifica Altre Rate", "End Incasso");
		
	}
	
	@Test
	public void test_signal() {
		
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new CaricaRataStub(true, 777));
		
		HashMap<String, Object> paramters = new HashMap<>();
		paramters.put("idPratica", "ID1234567");
		paramters.put("soggettoDebitore", new SoggettoDebitore("1234", "Gianalessandro", "Marrone", 300));
		
		ProcessInstance processInstance = kieSession.startProcess("it.workshop.incasso", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Start Incasso", "Repeat Carica", "Carica Rata", "Repeat Incasso", "Incasso Rata");
		
		List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("alessandra", null);
		assertEquals(1, taskList.size());
		Long oldTaskId = taskList.get(0).getId();
		
		kieSession.signalEvent("cambioStatoRata", new Rata(false, 777));
		
		taskList = taskService.getTasksAssignedAsPotentialOwner("alessandra", null);
		assertEquals(1, taskList.size());
		Long taskId = taskList.get(0).getId();
		
		assertNotEquals(taskId, oldTaskId);
		
		taskService.claim(taskId, "alessandra");
		taskService.start(taskId, "alessandra");
		taskService.complete(taskId, "alessandra", new HashMap<>());
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Cambio Stato", "Verifica Altre Rate");
		
		kieSession.signalEvent("cambioStatoRata", new Rata(true, 777));
		
		taskList = taskService.getTasksAssignedAsPotentialOwner("alessandra", null);
		assertEquals(1, taskList.size());
		taskId = taskList.get(0).getId();
		
		taskService.claim(taskId, "alessandra");
		taskService.start(taskId, "alessandra");
		taskService.complete(taskId, "alessandra", new HashMap<>());
		
		assertProcessInstanceCompleted(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "End Incasso");
		
	}
	
	@Test
	public void test_timeout() {
		
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new CaricaRataStub(true, 777));
		
		HashMap<String, Object> paramters = new HashMap<>();
		paramters.put("idPratica", "ID1234567");
		paramters.put("soggettoDebitore", new SoggettoDebitore("1234", "Gianalessandro", "Marrone", 300));
		
		ProcessInstance processInstance = kieSession.startProcess("it.workshop.incasso", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Start Incasso", "Repeat Carica", "Carica Rata", "Repeat Incasso", "Incasso Rata");
		
		List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("alessandra", null);
		assertEquals(1, taskList.size());
		Long oldTaskId = taskList.get(0).getId();
		
		PseudoClockScheduler sessionClock = kieSession.getSessionClock();
		sessionClock.advanceTime(50, TimeUnit.DAYS);
		
		taskList = taskService.getTasksAssignedAsPotentialOwner("alessandra", null);
		assertEquals(1, taskList.size());
		Long taskId = taskList.get(0).getId();
		
		assertNotEquals(taskId, oldTaskId);
		
		taskService.claim(taskId, "alessandra");
		taskService.start(taskId, "alessandra");
		taskService.complete(taskId, "alessandra", new HashMap<>());
		
		assertProcessInstanceCompleted(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Verifica Altre Rate", "End Incasso");
		
	}

}
