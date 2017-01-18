package it.redhat.demo;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class ProcTest extends JbpmJUnitBaseTestCase {
	
	private static RuntimeEngine runtime;
	
	@Before
    public void setUp() {
        Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
        resources.put("it/redhat/demo/proc.bpmn2", ResourceType.BPMN2);

        createRuntimeManager(Strategy.PROCESS_INSTANCE, resources);
        runtime = getRuntimeEngine(ProcessInstanceIdContext.get());
    }
	
	@Test
	public void test() {
		
		KieSession ksession = runtime.getKieSession();
		ProcessInstance pi = ksession.startProcess("it.redhat.demo.proc");
		assertProcessInstanceNotActive(pi.getId(), ksession);
		
	}
	
	@After
    public void tearDown() {
        disposeRuntimeManager();
    }

}
