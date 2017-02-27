package it.redhat.demo.valuesetting;

import java.util.ArrayList;
import java.util.HashMap;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

public class DefaultValueSettingProcessTest extends JbpmJUnitBaseTestCase {

	protected final static String PROCESSES_BASE_PATH = "it/redhat/demo/";

	protected KieSession kieSession;
	protected RuntimeManager runtimeManager;
	protected RuntimeEngine runtimeEngine;

	public DefaultValueSettingProcessTest() {
		super(true, true);
	}

	@Before
	public void before() {

		System.setProperty("org.kie.server.controller.user", "fabio");
		System.setProperty("org.kie.server.controller.pwd", "fabio$739");

		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "defaultValueSetting.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

		kieSession.getWorkItemManager().registerWorkItemHandler("ProcessServerRest", new NoActionWorkItemHandlerStub());

	}

	@After
	public void after() {

		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();

	}

	@Test
	public void test() {

		ArrayList<Integer> processInstanceList = new ArrayList<>();
		processInstanceList.add(7);
		processInstanceList.add(3);
		processInstanceList.add(9);

		HashMap<String, Object> defaultValues = new HashMap<String, Object>();
		defaultValues.put("managerFeedback", "KO");

		HashMap<String, Object> params = new HashMap<>();

		params.put("psHost", "localhost");
		params.put("psPort", 8080);
		params.put("container", "it.redhat.demo:bpms-selection-process:1.1.0");
		params.put("processInstanceIdList", processInstanceList);
		params.put("defaultValues", defaultValues);

		ProcessInstance process = kieSession.startProcess("it.redhat.demo.defaultValueSetting", params);

		assertProcessInstanceCompleted(process.getId());
		assertNodeTriggered(process.getId(), "Start Settings Default Value", "Skip Empty", "Process Instance",
				"Start Process Instance Setting", "SetsMultipleProcessInstanceVariables",
				"End Process Instance Setting", "End Settings Default Value");

	}
	
	@Test
	public void test_emptyProcessInstanceList() {

		ArrayList<Integer> processInstanceList = new ArrayList<>();

		HashMap<String, Object> defaultValues = new HashMap<String, Object>();
		defaultValues.put("managerFeedback", "KO");

		HashMap<String, Object> params = new HashMap<>();

		params.put("psHost", "localhost");
		params.put("psPort", 8080);
		params.put("container", "it.redhat.demo:bpms-selection-process:1.1.0");
		params.put("processInstanceIdList", processInstanceList);
		params.put("defaultValues", defaultValues);

		ProcessInstance process = kieSession.startProcess("it.redhat.demo.defaultValueSetting", params);

		assertProcessInstanceCompleted(process.getId());
		assertNodeTriggered(process.getId(), "Start Settings Default Value", "Skip Empty", "Empty");

	}
	
	@Test
	public void test_emptyDefaultValues() {

		ArrayList<Integer> processInstanceList = new ArrayList<>();
		processInstanceList.add(7);
		processInstanceList.add(3);
		processInstanceList.add(9);

		HashMap<String, Object> defaultValues = new HashMap<String, Object>();

		HashMap<String, Object> params = new HashMap<>();

		params.put("psHost", "localhost");
		params.put("psPort", 8080);
		params.put("container", "it.redhat.demo:bpms-selection-process:1.1.0");
		params.put("processInstanceIdList", processInstanceList);
		params.put("defaultValues", defaultValues);

		ProcessInstance process = kieSession.startProcess("it.redhat.demo.defaultValueSetting", params);

		assertProcessInstanceCompleted(process.getId());
		assertNodeTriggered(process.getId(), "Start Settings Default Value", "Skip Empty", "Empty");

	}

}
