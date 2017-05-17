package it.redhat.demo.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
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

import it.redhat.demo.model.Command;
import it.redhat.demo.wih.IncreaseAttempts;
import it.redhat.demo.wih.InitTask;

public class StubbornRestClientTest extends JbpmJUnitBaseTestCase {
	
	private static final String MACCALLISTER = "maccallister";
	
	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private TaskService taskService;
	
	public StubbornRestClientTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		System.setProperty("drools.clockType", "pseudo");
		
		runtimeManager = createRuntimeManager("it/redhat/demo/stubborn-rest-client.bpmn2");	
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		taskService = runtimeEngine.getTaskService();
	
		kieSession.getWorkItemManager().registerWorkItemHandler("InitTask", new InitTask());
		kieSession.getWorkItemManager().registerWorkItemHandler("IncreaseAttempts", new IncreaseAttempts());
		
	}
	
	@After
	public void after() {
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
		
	}
	
	@Test
	public void test_happyPath() {
	
		// register happy path stub
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new HappyRestStub());
		
		Command command = new Command();
		command.setName("Fabio M.");
		command.setValue(739);
		command.setOption(false);
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("url", "http://localhost:8080/eap6-rest/command");
		params.put("method", "POST");
		params.put("content", command);
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.stubborn-rest-client", params);
		
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "InitTask", "Try Rest Call", "Rest", "Rest Call OK");
		
	}
	
	@Test
	public void test_happyPath_jsonContent() throws Exception {
	
		// register happy path stub
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new HappyRestStub());
		
		Command command = new Command();
		command.setName("Fabio M.");
		command.setValue(739);
		command.setOption(false);
		
		//{"name":"Fabio M.","value":739,"option":false}
		String json = new ObjectMapper().writer().writeValueAsString(command);
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("url", "http://localhost:8080/eap6-rest/command");
		params.put("method", "POST");
		params.put("content", json);
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.stubborn-rest-client", params);
		
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "InitTask", "Try Rest Call", "Rest", "Rest Call OK");
		
	}
	
	@Test
	public void test_exceptionOnRestCall() {
	
		// register exception thrower stub
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new WIRuntimeExceptionThrowerWid());
		
		Command command = new Command();
		command.setName("Fabio M.");
		command.setValue(739);
		command.setOption(false);
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("url", "http://localhost:8080/eap6-rest/command");
		params.put("method", "POST");
		params.put("content", command);
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.stubborn-rest-client", params);
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "InitTask", "Try Rest Call", "Rest", "RestException", "IncreaseAttempts", "Time / User", "Wait Time");
		
		PseudoClockScheduler sessionClock = kieSession.getSessionClock();
		sessionClock.advanceTime(1000, TimeUnit.SECONDS);
		
		assertProcessInstanceActive(pi.getId());
		assertNodeTriggered(pi.getId(), "Wait User");
		
		List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner(MACCALLISTER, "en-UK");
		assertEquals(1, tasksAssignedAsPotentialOwner.size());
		
		TaskSummary taskSummary = tasksAssignedAsPotentialOwner.get(0);
		
		Map<String, Object> taskContent = taskService.getTaskContent(taskSummary.getId());
		Object input = taskContent.get("inContent");
		
		assertNotNull(input);
		
		taskService.claim(taskSummary.getId(), MACCALLISTER);
		
		// register happy path stub
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new HappyRestStub());
		
		taskService.start(taskSummary.getId(), MACCALLISTER);
		
		HashMap<String, Object> output = new HashMap<>();
		output.put("outContent", input);
		
		taskService.complete(taskSummary.getId(), MACCALLISTER, output);
		
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "Rest Call OK");
		
	}

}
