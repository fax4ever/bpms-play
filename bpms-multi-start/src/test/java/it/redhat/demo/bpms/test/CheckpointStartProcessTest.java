package it.redhat.demo.bpms.test;

import java.util.Collections;
import java.util.List;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.bpms.strategy.ChooseCheckpointStrategy;
import it.redhat.demo.bpms.wid.UnwrapVariables;
import it.redhat.demo.bpms.wid.WrapVariables;

public class CheckpointStartProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final String MY_BUSINESS_KEY = "mybusinesskey";

	private static final String DEVELOPER_USER = "marco";

	private final static Logger LOG = LoggerFactory.getLogger(CheckpointStartProcessTest.class);
	
	private static final String PROCESS_FOLDER = "it/redhat/demo/bpms/process/";
	
	private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
	private TaskService taskService;
	private AuditService auditService;
	
	private CorrelationKeyFactory factory;
    
    public CheckpointStartProcessTest() {
        super(true, true);
    }
    
    @Before
	public void before() {

        runtimeManager = createRuntimeManager(PROCESS_FOLDER + "checkpoint-start.bpmn2");
        runtimeEngine = getRuntimeEngine();
        kieSession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();
        auditService = runtimeEngine.getAuditService();
        
        factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        
        kieSession.getWorkItemManager().registerWorkItemHandler("WrapVariables", new WrapVariables(new ChooseCheckpointStrategy()));
        kieSession.getWorkItemManager().registerWorkItemHandler("UnwrapVariables", new UnwrapVariables());

    }
    
    @After
	public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }
    
    @Test
	public void test_baseCase() {
    	
    	ProcessInstance pi = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.bpms.process.checkpoint-start", getCorrelationKey(), Collections.singletonMap("dataA", "--A--"));
    	long id = pi.getId();
    	
    	assertProcessInstanceActive(id);
    	assertNodeTriggered(id, "StartProcess", "Gateway 0", "User Task 1");
    	
    	List<TaskSummary> marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataB", "--B--"));
    	
    	assertProcessInstanceActive(id);
    	assertNodeTriggered(id, "Gateway 1", "User Task 2");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataC", "--C--"));
    	
    	assertProcessInstanceActive(id);
    	assertNodeTriggered(id, "Gateway 2", "User Task 3");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.emptyMap());
    	
    	assertProcessInstanceCompleted(id);
    	assertNodeTriggered(id, "EndProcess");
    	
    	List<? extends VariableInstanceLog> dataAVariableLogs = auditService.findVariableInstances(id, "dataA");
    	assertEquals(1, dataAVariableLogs.size());
    	assertEquals("--A--", dataAVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBVariableLogs = auditService.findVariableInstances(id, "dataB");
    	assertEquals(1, dataBVariableLogs.size());
    	assertEquals("--B--", dataBVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataCVariableLogs = auditService.findVariableInstances(id, "dataC");
    	assertEquals(1, dataCVariableLogs.size());
    	assertEquals("--C--", dataCVariableLogs.get(0).getValue());
    	
    	// cast to implementation
    	org.jbpm.process.audit.ProcessInstanceLog piLog = (org.jbpm.process.audit.ProcessInstanceLog) auditService.findProcessInstance(id);
    	assertEquals(MY_BUSINESS_KEY, piLog.getCorrelationKey());
    	
    	LOG.info("{} :: [{} {} {}]", piLog.getCorrelationKey(), dataAVariableLogs, dataBVariableLogs, dataCVariableLogs);
    	
    }
    
    private CorrelationKey getCorrelationKey() {
        return factory.newCorrelationKey(MY_BUSINESS_KEY);
    }

}
