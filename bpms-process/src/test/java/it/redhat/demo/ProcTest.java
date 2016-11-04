package it.redhat.demo;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;

public class ProcTest extends JbpmJUnitBaseTestCase {

	public ProcTest() {
		super(true, true);
	}
	
	@Test
	public void test() {
		
		createRuntimeManager("it/redhat/demo/proc.bpmn2");
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		KieSession kieSession = runtimeEngine.getKieSession();
		
		
		
	}
	
}
