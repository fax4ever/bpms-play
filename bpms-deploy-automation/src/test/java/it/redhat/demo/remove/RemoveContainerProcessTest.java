package it.redhat.demo.remove;

import java.util.HashMap;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.stub.NoActionWorkItemHandlerStub;

public class RemoveContainerProcessTest extends JbpmJUnitBaseTestCase {
	
	protected final static String PROCESSES_BASE_PATH = "it/redhat/demo/";
	
	protected KieSession kieSession;
	protected RuntimeManager runtimeManager;
	protected RuntimeEngine runtimeEngine;
	
	public RemoveContainerProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "remove-container.bpmn2");
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RemoveRestStub());
		kieSession.getWorkItemManager().registerWorkItemHandler("WaitTask", new NoActionWorkItemHandlerStub());
		
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
		params.put("serverId", "process-server");
		params.put("deploymentId", "it.redhat.demo:bpms-selection-process:1.0.0");
		
		ProcessInstance proc = kieSession.startProcess("it.redhat.demo.remove-container", params);
		
		assertProcessInstanceCompleted(proc.getId());
		assertNodeTriggered(proc.getId(), "Start Undeploy", "BC / PS undeploy fork", "BC / PS undeploy join");
		
	}
	
}
