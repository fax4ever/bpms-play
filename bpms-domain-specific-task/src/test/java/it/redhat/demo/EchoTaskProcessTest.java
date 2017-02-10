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

public class EchoTaskProcessTest extends JbpmJUnitBaseTestCase {
	
	Logger log = LoggerFactory.getLogger(EchoTaskProcessTest.class);
	
	private KieSession kieSession;
	private AuditService auditService;
	
	public EchoTaskProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {
	
		createRuntimeManager("it/redhat/demo/echo-task.bpmn2");
		
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		auditService = runtimeEngine.getAuditService();
		
		kieSession = runtimeEngine.getKieSession();
		kieSession.getWorkItemManager().registerWorkItemHandler("EchoDomainSpecificTask", new EchoDomainSpecificTask());
		
	}
	
	@Test
	public void test() {
		
		HashMap<String,Object> params = new HashMap<>();
		params.put("input", "Ciao");
		params.put("external", "Ext");
		
		ProcessInstance instance = kieSession.startProcess("it.redhat.demo.echo-task", params);
		
		assertProcessInstanceCompleted(instance.getId());
		
		for (VariableInstanceLog varIntance : auditService.findVariableInstances(instance.getId())) {
			log.info("at the moment {} the variable {} an instance of {} has been changed from {} to {}", varIntance.getDate(), varIntance.getVariableId(), varIntance.getClass(), varIntance.getOldValue(), varIntance.getValue());
			
			if (varIntance.getVariableId().equals("output")) {
				Assert.assertEquals("Ciao", varIntance.getValue());
			}
			
		}
		
	}


}
