package it.workshop;

import java.util.ArrayList;
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

public class RivalseTest extends JbpmJUnitBaseTestCase {
	
	private static final String PROCESS_FOLDER = "it/workshop/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private TaskService taskService;
	
	public RivalseTest() {
		super(true, true);
	}
	
	@Before
	public void before() {

		runtimeManager = createRuntimeManager(PROCESS_FOLDER + "rivalse.bpmn2");
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
	public void test_revoca() {
		
		HashMap<String, Object> paramters = new HashMap<>();
		paramters.put("idPratica", "ID1234567");
		
		ProcessInstance processInstance = kieSession.startProcess("it.workshop.rivalse", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "StartProcess", "Presa In Carico");
		
		kieSession.signalEvent("revoca", "Pratica non lavorabile. Codice 232.");
		
		assertProcessInstanceAborted(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "Revoca Pratica", "Start Revoca", "End Revoca");
		
	}
	
	@Test
	public void test_rifiuto() {
		
		HashMap<String, Object> paramters = new HashMap<>();
		paramters.put("idPratica", "ID1234567");
		
		ProcessInstance processInstance = kieSession.startProcess("it.workshop.rivalse", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "StartProcess", "Presa In Carico");
		
		List<TaskSummary> marcoTasks = taskService.getTasksAssignedAsPotentialOwner("marco", null);
		assertEquals(1, marcoTasks.size());
		
		TaskSummary task = marcoTasks.get(0);
		
		taskService.claim(task.getId(), "marco");
		taskService.start(task.getId(), "marco");
		
		paramters.clear();
		paramters.put("accettazione", false);
		
		taskService.complete(task.getId(), "marco", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "accettazione / rifiuto", "Conferma Rifiuto");
		
		kieSession.signalEvent("rifiuto", "Rifiuto accordato. Codice 121.");
		
		assertProcessInstanceCompleted(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "End Rifiuto");
		
	}
	
	@Test
	public void test_accettazione() {
		
		HashMap<String, Object> paramters = new HashMap<>();
		paramters.put("idPratica", "ID1234567");
		
		ProcessInstance processInstance = kieSession.startProcess("it.workshop.rivalse", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "StartProcess", "Presa In Carico");
		
		List<TaskSummary> marcoTasks = taskService.getTasksAssignedAsPotentialOwner("marco", null);
		assertEquals(1, marcoTasks.size());
		
		TaskSummary task = marcoTasks.get(0);
		
		taskService.claim(task.getId(), "marco");
		taskService.start(task.getId(), "marco");
		
		ArrayList<Object> soggettiDebitori = new ArrayList<>();
		soggettiDebitori.add(new SoggettoDebitore("1234567F", "Luigi", "Rossi", 1000));
		soggettiDebitori.add(new SoggettoDebitore("1234568G", "Andrea", "Bianchi", 400));
		
		paramters.clear();
		paramters.put("accettazione", true);
		paramters.put("soggettiDebitori", soggettiDebitori);
		
		taskService.complete(task.getId(), "marco", paramters);
		
		assertProcessInstanceCompleted(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "accettazione / rifiuto", "Lavorazione Soggetto Debitore", "End Accettazione");
		
	}

}
