package it.redhat.test;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

import stub.RESTWorkItemHandlerStub;

public class BasicAuthRestClientProcessTest extends JbpmJUnitBaseTestCase {
	
	private KieSession kieSession;

	public BasicAuthRestClientProcessTest() {
		super(true, true);
	}
	
	@Before
	public void init() {
		createRuntimeManager("it/redhat/test/basic-auth-rest-client.bpmn2");
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		this.kieSession = runtimeEngine.getKieSession();
		this.kieSession.getWorkItemManager().registerWorkItemHandler("AuthRest", new RESTWorkItemHandlerStub());
	}
	
	@Test
	public void test() {
		
		ProcessInstance proc = kieSession.startProcess("it.redhat.demo.basic-auth-rest-client");
		assertNodeTriggered(proc.getId(), "AuthRest");
		assertProcessInstanceCompleted(proc.getId());
		
	}

}
