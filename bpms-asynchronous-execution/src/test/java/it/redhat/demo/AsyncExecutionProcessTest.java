package it.redhat.demo;

import java.util.HashMap;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class AsyncExecutionProcessTest extends JbpmJUnitBaseTestCase {
	
	protected final static String PROCESSES_BASE_PATH = "it/redhat/demo/";
	
	protected KieSession kieSession;
	protected RuntimeManager runtimeManager;
	protected RuntimeEngine runtimeEngine;
	
	public AsyncExecutionProcessTest() {
		super(true, true);
	}
	
	@Before
	public void before() {

		runtimeManager = createRuntimeManager(PROCESSES_BASE_PATH + "async-execution-process.bpmn2");

		runtimeEngine = getRuntimeEngine();
		kieSession = runtimeEngine.getKieSession();
		
		// we need to stub the async executor wid
		// because the test runtime don't see jms subsystem
		kieSession.getWorkItemManager().registerWorkItemHandler("AsyncCommand", new WorkItemHandler() {
			
			@Override
			public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
				workItemManager.completeWorkItem(workItem.getId(), new HashMap<>());
			}
			
			@Override
			public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
				
			}
			
		});

	}
	
	@After
	public void after() {

		runtimeManager.disposeRuntimeEngine(runtimeEngine);
		runtimeManager.close();

	}
	
	@Test
	public void test() {
		
		ProcessInstance pi = kieSession.startProcess("it.redhat.demo.async-execution-process");
		
		// because the stub is sync
		assertProcessInstanceCompleted(pi.getId());
		
	}

}
