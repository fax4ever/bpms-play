package it.redhat.demo.abort;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import java.util.HashMap;

public class AbortProcInstancesProcessTest extends JbpmJUnitBaseTestCase {
	
	protected final static String PROCESSES_BASE_PATH = "it/redhat/demo/";
	
	protected KieSession kieSession;
	protected RuntimeManager runtimeManager;
	protected RuntimeEngine runtimeEngine;
	
	public AbortProcInstancesProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "abort-active-instances.bpmn2");
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new AbortRestStub());
		
	}
	
	@After
	public void after() {
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
		
	}
	
	@Test
	public void test() {
		
		HashMap<String,Object> params = new HashMap<>();
		
		params.put("bcHost", "localhost");
		params.put("bcPort", 8230l);
		params.put("deploymentId", "it.redhat.demo:bpms-selection-process:1.0.0");
		
		ProcessInstance proc = kieSession.startProcess("it.redhat.demo.abort-active-instances", params);
		
		assertProcessInstanceCompleted(proc.getId());
		
	}

}
