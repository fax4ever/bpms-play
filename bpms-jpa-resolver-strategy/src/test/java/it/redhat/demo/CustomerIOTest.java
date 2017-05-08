package it.redhat.demo;

import java.util.HashMap;
import java.util.List;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

import it.redhat.demo.entity.Customer;

/**
 * Created by fabio.ercoli@redhat.com on 17/03/17.
 */
public class CustomerIOTest extends JbpmJUnitBaseTestCase {

	private static final String MACCALLISTER = "maccallister";

	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";

    private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
	private TaskService taskService;
    
    public CustomerIOTest() {
        super(true, true);
    }
    
    @Before
    public void before() {

        runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "customer-io.bpmn2");
        runtimeEngine = getRuntimeEngine();
        
        kieSession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();

    }
    
    @After
    public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }
    
    @Test
	public void test() {
    	
    	ProcessInstance pi = kieSession.startProcess("it.redhat.demo.customer-io");
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "CustomerUserTask");
    	
    	List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner(MACCALLISTER, "");
    	Assert.assertEquals(1, tasksAssignedAsPotentialOwner.size());
    	
    	Customer customer = new Customer();
    	customer.setName("Fabio Massimo");
    	customer.setPremium(true);
    	
    	TaskSummary task = tasksAssignedAsPotentialOwner.get(0);
    	HashMap<String,Object> outputParameters = new HashMap<>();
    	outputParameters.put("customer", customer);
 
    	taskService.claim(task.getId(), MACCALLISTER);
    	taskService.start(task.getId(), MACCALLISTER);
    	taskService.complete(task.getId(), MACCALLISTER, outputParameters);
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "EndProcess");
    	
    }

}
