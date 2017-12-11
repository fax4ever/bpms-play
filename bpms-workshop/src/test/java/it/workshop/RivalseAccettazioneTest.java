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
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

import it.workshop.model.SoggettoDebitore;

public class RivalseAccettazioneTest extends JbpmJUnitBaseTestCase {
	
	private static final String PROCESS_FOLDER = "it/workshop/";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private TaskService taskService;
	private AuditService auditService;
	
	public RivalseAccettazioneTest() {
		super(true, true);
	}
	
	@Before
	public void before() {

		runtimeManager = createRuntimeManager(PROCESS_FOLDER + "rivalse.bpmn2", PROCESS_FOLDER + "recupero.bpmn2", PROCESS_FOLDER + "incasso.bpmn2");
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
		
		paramters.clear();
		paramters.put("accettazione", true);
		paramters.put("soggettiDebitori", soggettiDebitori);
		
		taskService.complete(task.getId(), "marco", paramters);
		
		assertProcessInstanceActive(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "accettazione / rifiuto", "Lavorazione Soggetto Debitore", "Start Lavorazione", "Recupero");
		
		// go inside Recupero!
		List<? extends ProcessInstanceLog> subProcs = auditService.findSubProcessInstances(processInstance.getId());
		assertEquals(1, subProcs.size());
		Long subId = subProcs.get(0).getProcessInstanceId();
		
		assertProcessInstanceActive(subId);
		assertNodeTriggered(subId, "Start Recupero", "Restart", "Abbandono / Recupero");
		
		marcoTasks = taskService.getTasksAssignedAsPotentialOwner("marco", null);
		assertEquals(1, marcoTasks.size());
		task = marcoTasks.get(0);
		
		taskService.claim(task.getId(), "marco");
		taskService.start(task.getId(), "marco");
		
		paramters.clear();
		paramters.put("recupero", false);
		
		taskService.complete(task.getId(), "marco", paramters);
		
		
		
		
	}

}
