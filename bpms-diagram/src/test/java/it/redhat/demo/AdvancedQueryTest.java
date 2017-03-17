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
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

/**
 * Created by fabio.ercoli@redhat.com on 17/03/17.
 */
public class AdvancedQueryTest extends JbpmJUnitBaseTestCase {

	private static final String MACCALLISTER = "maccallister";
	private static final String COLTRINARI = "coltrinari";

	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";

    private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
	private TaskService taskService;
	private AuditService auditService;
    
    public AdvancedQueryTest() {
        super(true, true);
    }
    
    @Before
    public void before() {

        runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "diagram.bpmn2");
        runtimeEngine = getRuntimeEngine();
        
        kieSession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();
        auditService = runtimeEngine.getAuditService();

    }
    
    @After
    public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }
    
    @Test
	public void test() {
    	
    	ProcessInstance pi = kieSession.startProcess("it.redhat.demo.diagram");
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "StartProcess", "Make Choise");
    	
    	List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner(MACCALLISTER, "");
    	Assert.assertEquals(1, tasksAssignedAsPotentialOwner.size());
    	
    	TaskSummary task = tasksAssignedAsPotentialOwner.get(0);
    	HashMap<String,Object> outputParameters = new HashMap<>();
    	outputParameters.put("choise", true);
 
    	taskService.claim(task.getId(), MACCALLISTER);
    	taskService.start(task.getId(), MACCALLISTER);
    	taskService.complete(task.getId(), MACCALLISTER, outputParameters);
    	
    	assertProcessInstanceActive(pi.getId());
    	assertNodeTriggered(pi.getId(), "choise", "Write Moment");
    	
    	tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner(COLTRINARI, "");
    	Assert.assertEquals(1, tasksAssignedAsPotentialOwner.size());
    	
    	task = tasksAssignedAsPotentialOwner.get(0);
    	outputParameters.clear();
    	outputParameters.put("text", "bla bla bla...");
    	
    	taskService.claim(task.getId(), COLTRINARI);
    	taskService.start(task.getId(), COLTRINARI);
    	taskService.complete(task.getId(), COLTRINARI, outputParameters);
    	
    	assertProcessInstanceCompleted(pi.getId());
    	assertNodeTriggered(pi.getId(), "End Event 1");
    	
    	ProcessInstanceLog auditPI = auditService.findProcessInstance(pi.getId());
    	assertNotNull(auditPI);
    	
    }

}
