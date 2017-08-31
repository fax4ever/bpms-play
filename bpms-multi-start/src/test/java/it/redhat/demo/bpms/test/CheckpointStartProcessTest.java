package it.redhat.demo.bpms.test;

import java.util.Collections;
import java.util.HashSet;
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
import it.redhat.demo.bpms.wid.RestartProcessInstance;

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
        
        kieSession.getWorkItemManager().registerWorkItemHandler("RestartProcessInstance", new RestartProcessInstance(kieSession, new ChooseCheckpointStrategy()));

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
    
    @Test
    public void restart_from_checkpoint_1() {
    	
    	ProcessInstance originalProcessInstance = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.bpms.process.checkpoint-start", getCorrelationKey(), Collections.singletonMap("dataA", "--A--"));
    	long originalProcessInstanceId = originalProcessInstance.getId();
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "StartProcess", "Gateway 0", "User Task 1");
    	
    	List<TaskSummary> marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataB", "--B--"));
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Gateway 1", "User Task 2");
    	
    	kieSession.signalEvent("restart", "..A..");
    	
    	// original process instance will be aborted
    	assertProcessInstanceAborted(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Event Handler", "Restart", "Restart Process Instance", "RestartEnd");
    	
    	// a new process instance will be created
    	long newProcessInstanceId = originalProcessInstanceId + 1;
    	
    	assertProcessInstanceActive(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "StartProcess", "Gateway 0", "Gateway 1", "User Task 2");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataC", "--C--"));
    	
    	assertProcessInstanceActive(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "Gateway 2", "User Task 3");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.emptyMap());
    	
    	assertProcessInstanceCompleted(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "EndProcess");
    	
    	HashSet<String> aValues = new HashSet<String>();
    	aValues.add("--A--");
    	aValues.add("..A..");
    	
    	List<? extends VariableInstanceLog> dataAVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataA");
    	assertEquals(2, dataAVariableLogs.size());
    	assertTrue(aValues.contains(dataAVariableLogs.get(0).getValue()));
    	assertTrue(aValues.contains(dataAVariableLogs.get(1).getValue()));
    	
    	List<? extends VariableInstanceLog> dataBVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataB");
    	assertEquals(1, dataBVariableLogs.size());
    	assertEquals("--B--", dataBVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataANewVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataA");
    	assertEquals(1, dataANewVariableLogs.size());
    	assertEquals("..A..", dataANewVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBNewVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataB");
    	assertEquals(1, dataBNewVariableLogs.size());
    	assertEquals("--B--", dataBNewVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataCNewVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataC");
    	assertEquals(1, dataCNewVariableLogs.size());
    	assertEquals("--C--", dataCNewVariableLogs.get(0).getValue());
    	
    	// cast to implementation
    	org.jbpm.process.audit.ProcessInstanceLog piLog = (org.jbpm.process.audit.ProcessInstanceLog) auditService.findProcessInstance(originalProcessInstanceId);
    	assertEquals(MY_BUSINESS_KEY, piLog.getCorrelationKey());
    	
    	// cast to implementation
    	piLog = (org.jbpm.process.audit.ProcessInstanceLog) auditService.findProcessInstance(newProcessInstanceId);
    	assertEquals("..A..", piLog.getCorrelationKey());
    	
    }
    
    @Test
    public void restart_from_checkpoint_2() {
    	
    	ProcessInstance originalProcessInstance = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.bpms.process.checkpoint-start", getCorrelationKey(), Collections.singletonMap("dataA", "--A--"));
    	long originalProcessInstanceId = originalProcessInstance.getId();
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "StartProcess", "Gateway 0", "User Task 1");
    	
    	List<TaskSummary> marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataB", "--B--"));
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Gateway 1", "User Task 2");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataC", "--C--"));
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Gateway 2", "User Task 3");
    	
    	kieSession.signalEvent("restart", "..A..");
    	
    	// original process instance will be aborted
    	assertProcessInstanceAborted(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Event Handler", "Restart", "Restart Process Instance", "RestartEnd");
    	
    	// a new process instance will be created
    	long newProcessInstanceId = originalProcessInstanceId + 1;
    	
    	assertProcessInstanceActive(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "StartProcess", "Gateway 0", "Gateway 2", "User Task 3");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.emptyMap());
    	
    	assertProcessInstanceCompleted(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "EndProcess");
    	
    	HashSet<String> aValues = new HashSet<String>();
    	aValues.add("--A--");
    	aValues.add("..A..");
    	
    	List<? extends VariableInstanceLog> dataAVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataA");
    	assertEquals(2, dataAVariableLogs.size());
    	assertTrue(aValues.contains(dataAVariableLogs.get(0).getValue()));
    	assertTrue(aValues.contains(dataAVariableLogs.get(1).getValue()));
    	
    	List<? extends VariableInstanceLog> dataBVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataB");
    	assertEquals(1, dataBVariableLogs.size());
    	assertEquals("--B--", dataBVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataANewVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataA");
    	assertEquals(1, dataANewVariableLogs.size());
    	assertEquals("..A..", dataANewVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBNewVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataB");
    	assertEquals(1, dataBNewVariableLogs.size());
    	assertEquals("--B--", dataBNewVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataCNewVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataC");
    	assertEquals(1, dataCNewVariableLogs.size());
    	assertEquals("--C--", dataCNewVariableLogs.get(0).getValue());
    	
    	// cast to implementation
    	org.jbpm.process.audit.ProcessInstanceLog piLog = (org.jbpm.process.audit.ProcessInstanceLog) auditService.findProcessInstance(originalProcessInstanceId);
    	assertEquals(MY_BUSINESS_KEY, piLog.getCorrelationKey());
    	
    	// cast to implementation
    	piLog = (org.jbpm.process.audit.ProcessInstanceLog) auditService.findProcessInstance(newProcessInstanceId);
    	assertEquals("..A..", piLog.getCorrelationKey());
    	
    }
    
    private CorrelationKey getCorrelationKey() {
        return factory.newCorrelationKey(MY_BUSINESS_KEY);
    }

}
