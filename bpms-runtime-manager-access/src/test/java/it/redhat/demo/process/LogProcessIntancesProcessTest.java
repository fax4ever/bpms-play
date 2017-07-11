package it.redhat.demo.process;

import java.util.HashMap;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.stub.LogProcessInstancesStub;

public class LogProcessIntancesProcessTest extends JbpmJUnitBaseTestCase {
	
	private KieSession kieSession;

	public LogProcessIntancesProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		createRuntimeManager("it/redhat/demo/log-process-instance.bpmn2");
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		kieSession.getWorkItemManager().registerWorkItemHandler("LogProcessInstances", new LogProcessInstancesStub());
		
	}
	
	@Test
	public void test() {
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("containerId", "main");
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.log-process-instance", params);
		assertProcessInstanceCompleted(pi.getId());
		
	}

}
