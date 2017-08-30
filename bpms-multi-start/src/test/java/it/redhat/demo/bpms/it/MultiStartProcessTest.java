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
package it.redhat.demo.bpms.it;

import java.util.HashMap;
import java.util.List;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fabio Massimo Ercoli (C) 2017 Red Hat Inc.
 */
public class MultiStartProcessTest extends JbpmJUnitBaseTestCase {
	
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

    }
    
    @After
    public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }
    
    @Test
	public void test_baseCase() {
    	
    	ProcessInstance pi = kieSession.startProcess("it.redhat.demo.bpms.process.multi-start", new HashMap<>());
    	long id = pi.getId();
    	
    	assertProcessInstanceActive(id);
    	assertNodeTriggered(id, "StartProcess", "User Task 1");
    	
    	List<TaskSummary> marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, new HashMap<>());
    	
    	assertProcessInstanceActive(id);
    	assertNodeTriggered(id, "User Task 2");
    	
    	marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
    	assertEquals(1, marcoTaskList.size());
    	
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, new HashMap<>());
    	
    	assertProcessInstanceCompleted(id);
    	assertNodeTriggered(id, "End Event 1");
    	
    }
    
    @Test
    public void start_event() {
    	
    	ProcessInstance originalProcessInstance = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.bpms.process.multi-start", getCorrelationKey(), new HashMap<>());
    	long originalProcessInstanceId = originalProcessInstance.getId();
    	
    	assertProcessInstanceActive(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "StartProcess", "User Task 1");
    	
    	kieSession.signalEvent("start", "GOGOGO");
    	
    	List<? extends ProcessInstanceLog> processInstances = auditService.findProcessInstances();
    	LOG.info("Process Instances: {}", processInstances);
    	assertEquals(2, processInstances.size());
    	
    	assertProcessInstanceAborted(originalProcessInstanceId);
    	assertNodeTriggered(originalProcessInstanceId, "Event Handler", "Start Event 3", "Script Task 1", "End Event 3");
    	
    	long newProcessInstanceId = originalProcessInstanceId + 1;
    	
		assertProcessInstanceActive(newProcessInstanceId);
		assertNodeTriggered(newProcessInstanceId, "Start Event 5", "Exclusive Gateway 1", "User Task 2");
		
		List<TaskSummary> marcoTaskList = taskService.getTasksAssignedAsPotentialOwner(DEVELOPER_USER, null);
		
		LOG.info("Task List: {}", marcoTaskList);
    	assertEquals(1, marcoTaskList.size());
    	
    	taskService.start(marcoTaskList.get(0).getId(), DEVELOPER_USER);
    	taskService.complete(marcoTaskList.get(0).getId(), DEVELOPER_USER, new HashMap<>());
    	
    	assertProcessInstanceCompleted(newProcessInstanceId);
    	assertNodeTriggered(newProcessInstanceId, "End Event 1");
    	
    }
    
    private CorrelationKey getCorrelationKey() {
        return factory.newCorrelationKey("mybusinesskey");
    }
	
}
