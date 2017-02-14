package it.redhat.demo;

import java.util.HashMap;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterTaskProcessTest extends JbpmJUnitBaseTestCase {
	
	Logger log = LoggerFactory.getLogger(ParameterTaskProcessTest.class);
	
	private KieSession kieSession;
	private AuditService auditService;
	
	public ParameterTaskProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
	
		createRuntimeManager("it/redhat/demo/parameter-task.bpmn2");
		
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		auditService = runtimeEngine.getAuditService();
		
		kieSession = runtimeEngine.getKieSession();
		kieSession.getWorkItemManager().registerWorkItemHandler("ParameterDomainSpecificTask", new ParameterDomainSpecificTask());
		
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
		
		ProcessInstance instance = kieSession.startProcess("it.redhat.demo.parameter-task", params);
		
		assertProcessInstanceCompleted(instance.getId());
		
		for (VariableInstanceLog varIntance : auditService.findVariableInstances(instance.getId())) {
			log.info("at the moment {} the variable {} an instance of {} has been changed from {} to {}", varIntance.getDate(), varIntance.getVariableId(), varIntance.getClass(), varIntance.getOldValue(), varIntance.getValue());
			
			if (varIntance.getVariableId().equals("output")) {
				Assert.assertEquals("Ciao", varIntance.getValue());
			}
			
		}
		
	}

}
