package it.redhat.demo;

import it.redhat.demo.listener.LogProcessEventListener;
import it.redhat.demo.listener.LogTaskEventListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProcessTest extends JbpmJUnitBaseTestCase {

    private static final String MACCALLISTER = "maccallister";

    private static final String IT_REDHAT_DEMO = "it/redhat/demo/";

    private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
    private TaskService taskService;
    private AuditService auditService;

    private CorrelationKeyFactory factory;

    public UserProcessTest() {
        super(true, true);
        factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
    }

    @Before
    public void before() {

        LogProcessEventListener listener = new LogProcessEventListener(false);
        addProcessEventListener(listener);

        LogTaskEventListener taskEventListener = new LogTaskEventListener(false);
        addTaskEventListener(taskEventListener);

        runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "user-parent-process.bpmn2", IT_REDHAT_DEMO + "user-sub-process.bpmn2");
        runtimeEngine = getRuntimeEngine();
        kieSession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();
        auditService = runtimeEngine.getAuditService();

        listener.setRuntimeManager(runtimeManager);
        taskEventListener.setRuntimeManager(runtimeManager);

    }

    @After
    public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }

    @Test
    public void test() {

        ProcessInstance pi = ((CorrelationAwareProcessRuntime) kieSession)
                .startProcess("it.redhat.demo.user-parent-process", getCorrelationKey(), null);

        assertProcessInstanceActive(pi.getId());
        assertNodeTriggered(pi.getId(), "StartProcess", "CallSubprocess");

        List<? extends ProcessInstanceLog> subProcessInstances = auditService.findSubProcessInstances(pi.getId());
        assertEquals(1, subProcessInstances.size());
        ProcessInstanceLog subPi = subProcessInstances.get(0);

        assertProcessInstanceActive(subPi.getProcessInstanceId());
        assertNodeTriggered(subPi.getProcessInstanceId(), "StartProcess", "IOUserTask");

        List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner(MACCALLISTER,
                "");
        assertEquals(1, tasksAssignedAsPotentialOwner.size());

        TaskSummary task = tasksAssignedAsPotentialOwner.get(0);

        taskService.claim(task.getId(), MACCALLISTER);
        taskService.start(task.getId(), MACCALLISTER);

        Map<String, Object> taskContent = taskService.getTaskContent(task.getId());
        assertTrue(taskContent.containsKey("input"));
        String input = (String) taskContent.get("input");

        HashMap<String, Object> data = new HashMap<>();
        data.put("output", "hello " + input);

        taskService.complete(task.getId(), MACCALLISTER, data);
        assertProcessInstanceCompleted(subPi.getProcessInstanceId());
        assertNodeTriggered(subPi.getProcessInstanceId(), "EndProcess");

        assertProcessInstanceCompleted(pi.getId());
        assertNodeTriggered(pi.getId(), "EndProcess");

    }

    private CorrelationKey getCorrelationKey() {
        List<String> properties = new ArrayList<String>();
        properties.add("customerid");
        properties.add("orderid");
        return factory.newCorrelationKey(properties);
    }

}
