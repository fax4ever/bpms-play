package it.redhat.test;

import java.util.HashMap;
import java.util.List;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

import it.redhat.demo.customtask.ChooseDeployStrategy;
import it.redhat.demo.customtask.CreateContainerSpec;
import it.redhat.test.stub.RestStub;
import it.redhat.test.stub.VerifyServerStub;

public class UnifiedManagedDeployTest extends JbpmJUnitBaseTestCase {
	
	protected KieSession kieSession;
	private AuditService auditService;
	
	public UnifiedManagedDeployTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
		
		createRuntimeManager("it/redhat/test/create-container.bpmn2", "it/redhat/test/update-container.bpmn2", "it/redhat/test/migration.bpmn2", "it/redhat/test/unified-managed-deploy.bpmn2");
		
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		auditService = runtimeEngine.getAuditService();
		registerWorkItemHandler();
		
	}

	protected void registerWorkItemHandler() {
		kieSession.getWorkItemManager().registerWorkItemHandler("Rest", new RestStub());
		kieSession.getWorkItemManager().registerWorkItemHandler("ProcessServerRest", new VerifyServerStub());
		kieSession.getWorkItemManager().registerWorkItemHandler("ChooseDeployStrategy", new ChooseDeployStrategy());
		kieSession.getWorkItemManager().registerWorkItemHandler("CreateContainerSpec", new CreateContainerSpec());
	}
	
	@Test
	public void test() {
		
		HashMap<String,Object> params = new HashMap<>();
		
		params.put("bcHost", "localhost");
		params.put("bcPort", "8230");
		params.put("serverId", "process-server");
		params.put("groupId", "it.redhat.demo");
		params.put("artifactId", "bpms-rest-task");
		params.put("version", "1.1.0-SNAPSHOT");
		
		ProcessInstance instance = kieSession.startProcess("it.redhat.test.unified-managed-deploy", params);
		assertProcessInstanceCompleted(instance.getId());
		assertNodeTriggered(instance.getId(), "StartProcess", "Read Server Template", "ChooseDeployStrategy", "Create / Update", "Create Container", "Migration / Not", "NEW Release");
		
		List<? extends ProcessInstanceLog> subProcessInstances = auditService.findSubProcessInstances(instance.getId());
		assertEquals(1, subProcessInstances.size());
		ProcessInstanceLog createContainerProcessInstance = subProcessInstances.get(0);
		
		assertNodeTriggered(createContainerProcessInstance.getProcessInstanceId(), 
				"StartProcess", "Bc-Ps fork start", "Bc-Deploy", "CreateContainerSpec", "Ps-Deploy", "Ps-Deploy", "Bc-Ps fork end", "EndProcess",
				"StartVerify", "verify server gateway", "verify server gateway", "verify server gateway", "EndVerify");
		
	}

}
