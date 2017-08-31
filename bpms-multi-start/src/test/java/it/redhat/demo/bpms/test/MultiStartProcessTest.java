/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
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

/**
 * @author Fabio Massimo Ercoli (C) 2017 Red Hat Inc.
 */
public class MultiStartProcessTest extends JbpmJUnitBaseTestCase {
	
	private static final String MY_BUSINESS_KEY = "mybusinesskey";

	private static final String DEVELOPER_USER = "marco";

	private final static Logger LOG = LoggerFactory.getLogger(MultiStartProcessTest.class);
	
	private static final String PROCESS_FOLDER = "it/redhat/demo/bpms/process/";
	
	private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
	private TaskService taskService;
	private AuditService auditService;
	
	private CorrelationKeyFactory factory;
    
    public MultiStartProcessTest() {
        super(true, true);
    }
    
    @Before
	public void before() {

        runtimeManager = createRuntimeManager(PROCESS_FOLDER + "multi-start.bpmn2");
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
    	
    	ProcessInstance pi = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.bpms.process.multi-start", getCorrelationKey(), Collections.singletonMap("dataA", "--A--"));
    	long id = pi.getId();
    	
    	assertProcessInstanceActive(id);
    	assertNodeTriggered(id, "StartProcess", "User Task 1");
    	
    	List<TaskSummary> marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataB", "--B--"));
    	
    	assertProcessInstanceActive(id);
    	assertNodeTriggered(id, "Exclusive Gateway 1", "User Task 2");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataC", "--C--"));
    	
    	assertProcessInstanceActive(id);
    	assertNodeTriggered(id, "Exclusive Gateway 2", "User Task 3");
    	
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
    	
    	ProcessInstance originalProcessInstance = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.bpms.process.multi-start", getCorrelationKey(), Collections.singletonMap("dataA", "--A--"));
    	long originalProcessInstanceId = originalProcessInstance.getId();
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "StartProcess", "User Task 1");
    	
    	List<TaskSummary> marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataB", "--B--"));
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Exclusive Gateway 1", "User Task 2");
    	
    	kieSession.signalEvent("restart", 1);
    	
    	// original process instance will be aborted
    	assertProcessInstanceAborted(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Event Handler", "Restart", "Wrap Variables", "Exclusive Gateway 3", "Restart Checkpoint 1");
    	
    	// a new process instance will be created
    	long newProcessInstanceId = originalProcessInstanceId + 1;
    	
    	assertProcessInstanceActive(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "Start Checkpoint 1", "Unwrap Variables", "Exclusive Gateway 1", "User Task 2");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataC", "--C--"));
    	
    	assertProcessInstanceActive(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "Exclusive Gateway 2", "User Task 3");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.emptyMap());
    	
    	assertProcessInstanceCompleted(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "EndProcess");
    	
    	List<? extends VariableInstanceLog> dataAOldVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataA");
    	assertEquals(1, dataAOldVariableLogs.size());
    	assertEquals("--A--", dataAOldVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBOldVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataB");
    	assertEquals(1, dataBOldVariableLogs.size());
    	assertEquals("--B--", dataBOldVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataAVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataA");
    	assertEquals(1, dataAVariableLogs.size());
    	assertEquals("--A--", dataAVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataB");
    	assertEquals(1, dataBVariableLogs.size());
    	assertEquals("--B--", dataBVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataCVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataC");
    	assertEquals(1, dataCVariableLogs.size());
    	assertEquals("--C--", dataCVariableLogs.get(0).getValue());
    	
    }
    
    @Test
    public void restart_from_checkpoint_1_implicit() {
    	
    	ProcessInstance originalProcessInstance = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.bpms.process.multi-start", getCorrelationKey(), Collections.singletonMap("dataA", "--A--"));
    	long originalProcessInstanceId = originalProcessInstance.getId();
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "StartProcess", "User Task 1");
    	
    	List<TaskSummary> marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataB", "--B--"));
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Exclusive Gateway 1", "User Task 2");
    	
    	kieSession.signalEvent("restart", 0);
    	
    	// original process instance will be aborted
    	assertProcessInstanceAborted(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Event Handler", "Restart", "Wrap Variables", "Exclusive Gateway 3", "Restart Checkpoint 1");
    	
    	// a new process instance will be created
    	long newProcessInstanceId = originalProcessInstanceId + 1;
    	
    	assertProcessInstanceActive(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "Start Checkpoint 1", "Unwrap Variables", "Exclusive Gateway 1", "User Task 2");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataC", "--C--"));
    	
    	assertProcessInstanceActive(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "Exclusive Gateway 2", "User Task 3");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.emptyMap());
    	
    	assertProcessInstanceCompleted(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "EndProcess");
    	
    	List<? extends VariableInstanceLog> dataAOldVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataA");
    	assertEquals(1, dataAOldVariableLogs.size());
    	assertEquals("--A--", dataAOldVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBOldVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataB");
    	assertEquals(1, dataBOldVariableLogs.size());
    	assertEquals("--B--", dataBOldVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataAVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataA");
    	assertEquals(1, dataAVariableLogs.size());
    	assertEquals("--A--", dataAVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataB");
    	assertEquals(1, dataBVariableLogs.size());
    	assertEquals("--B--", dataBVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataCVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataC");
    	assertEquals(1, dataCVariableLogs.size());
    	assertEquals("--C--", dataCVariableLogs.get(0).getValue());
    	
    }
    
    @Test
    public void restart_from_checkpoint_2() {
    	
    	ProcessInstance originalProcessInstance = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.bpms.process.multi-start", getCorrelationKey(), Collections.singletonMap("dataA", "--A--"));
    	long originalProcessInstanceId = originalProcessInstance.getId();
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "StartProcess", "User Task 1");
    	
    	List<TaskSummary> marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataB", "--B--"));
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Exclusive Gateway 1", "User Task 2");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataC", "--C--"));
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Exclusive Gateway 2", "User Task 3");
    	
    	kieSession.signalEvent("restart", 2);
    	
    	// original process instance will be aborted
    	assertProcessInstanceAborted(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Event Handler", "Restart", "Wrap Variables", "Exclusive Gateway 3", "Restart Checkpoint 2");
    	
    	// a new process instance will be created
    	long newProcessInstanceId = originalProcessInstanceId + 1;
    	
    	assertProcessInstanceActive(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "Start Checkpoint 2", "Unwrap Variables", "Exclusive Gateway 2", "User Task 3");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.emptyMap());
    	
    	assertProcessInstanceCompleted(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "EndProcess");
    	
    	List<? extends VariableInstanceLog> dataAOldVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataA");
    	assertEquals(1, dataAOldVariableLogs.size());
    	assertEquals("--A--", dataAOldVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBOldVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataB");
    	assertEquals(1, dataBOldVariableLogs.size());
    	assertEquals("--B--", dataBOldVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataAVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataA");
    	assertEquals(1, dataAVariableLogs.size());
    	assertEquals("--A--", dataAVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataB");
    	assertEquals(1, dataBVariableLogs.size());
    	assertEquals("--B--", dataBVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataCVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataC");
    	assertEquals(1, dataCVariableLogs.size());
    	assertEquals("--C--", dataCVariableLogs.get(0).getValue());
    	
    }
    
    @Test
    public void restart_from_checkpoint_2_implicit() {
    	
    	ProcessInstance originalProcessInstance = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.bpms.process.multi-start", getCorrelationKey(), Collections.singletonMap("dataA", "--A--"));
    	long originalProcessInstanceId = originalProcessInstance.getId();
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "StartProcess", "User Task 1");
    	
    	List<TaskSummary> marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataB", "--B--"));
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Exclusive Gateway 1", "User Task 2");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.singletonMap("dataC", "--C--"));
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Exclusive Gateway 2", "User Task 3");
    	
    	kieSession.signalEvent("restart", 0);
    	
    	// original process instance will be aborted
    	assertProcessInstanceAborted(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Event Handler", "Restart", "Wrap Variables", "Exclusive Gateway 3", "Restart Checkpoint 2");
    	
    	// a new process instance will be created
    	long newProcessInstanceId = originalProcessInstanceId + 1;
    	
    	assertProcessInstanceActive(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "Start Checkpoint 2", "Unwrap Variables", "Exclusive Gateway 2", "User Task 3");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, Collections.emptyMap());
    	
    	assertProcessInstanceCompleted(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "EndProcess");
    	
    	List<? extends VariableInstanceLog> dataAOldVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataA");
    	assertEquals(1, dataAOldVariableLogs.size());
    	assertEquals("--A--", dataAOldVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBOldVariableLogs = auditService.findVariableInstances(originalProcessInstanceId, "dataB");
    	assertEquals(1, dataBOldVariableLogs.size());
    	assertEquals("--B--", dataBOldVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataAVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataA");
    	assertEquals(1, dataAVariableLogs.size());
    	assertEquals("--A--", dataAVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataBVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataB");
    	assertEquals(1, dataBVariableLogs.size());
    	assertEquals("--B--", dataBVariableLogs.get(0).getValue());
    	
    	List<? extends VariableInstanceLog> dataCVariableLogs = auditService.findVariableInstances(newProcessInstanceId, "dataC");
    	assertEquals(1, dataCVariableLogs.size());
    	assertEquals("--C--", dataCVariableLogs.get(0).getValue());
    	
    }
    
    private CorrelationKey getCorrelationKey() {
        return factory.newCorrelationKey(MY_BUSINESS_KEY);
    }
	
}
