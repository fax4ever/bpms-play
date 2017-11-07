package it.redhat.demo;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.redhat.demo.listener.LogProcessEventListener;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;

public class AsyncScriptProcessTest extends JbpmJUnitBaseTestCase {

	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";

	private RuntimeManager runtimeManager;
	private RuntimeEngine runtimeEngine;
	private KieSession kieSession;
	private AuditService auditService;

	private CorrelationKeyFactory factory;

	public AsyncScriptProcessTest() {
		super(true, true);
		factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
	}
	
	@Before
	public void before() {

		LogProcessEventListener listener = new LogProcessEventListener(false);
		addProcessEventListener(listener);

		runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "async-script-parent-process.bpmn2", IT_REDHAT_DEMO + "script-sub-process.bpmn2");
		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		auditService = runtimeEngine.getAuditService();

		listener.setRuntimeManager(runtimeManager);
			
	}
	
	@After
	public void after() {
		
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();
		
	}
	
	@Test
	public void test() {

		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("processId", "it.redhat.demo.script-parent-process");

		ProcessInstance pi = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.async-script-parent-process", getCorrelationKey(), parameters);
		
		assertProcessInstanceCompleted(pi.getId());
		assertNodeTriggered(pi.getId(), "StartProcess", "CallSubprocess", "EndProcess");
		
		List<? extends ProcessInstanceLog> subProcessInstances = auditService.findSubProcessInstances(pi.getId());
		assertEquals(1, subProcessInstances.size());
		ProcessInstanceLog subPi = subProcessInstances.get(0);
		
		assertProcessInstanceCompleted(subPi.getProcessInstanceId());
		assertNodeTriggered(subPi.getProcessInstanceId(), "StartProcess", "ScriptTask", "EndProcess");
		
	}

	private CorrelationKey getCorrelationKey() {
		return factory.newCorrelationKey("mybusinesskey");
	}

}
