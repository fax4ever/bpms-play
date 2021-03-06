package it.redhat.demo.bpm.process;

import java.util.HashMap;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

public class SignalProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final String PROCESS_FOLDER = "it/redhat/demo/bpm/process/";

	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	
	public SignalProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {

		createRuntimeManager(PROCESS_FOLDER + "signal.bpmn2");
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();

	}

	@After
	public void after() {

		manager.disposeRuntimeEngine(runtimeEngine);
		manager.close();

	}
	
	@Test
	public void test() {
		
		HashMap<String, Object> parameters = new HashMap<>();
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.bpm.process.signal", parameters);
		
		assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "MySignalCatchEvent");
    	
    	kieSession.signalEvent("mySignal", "mySignalContent", pi.getId());
    	
    	kieSession.signalEvent("anotherSignal", "anotherSignalContent", pi.getId());
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "EndProcess");
		
    	
	}

}
