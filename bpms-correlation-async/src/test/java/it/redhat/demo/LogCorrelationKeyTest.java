package it.redhat.demo;

import org.drools.core.time.impl.PseudoClockScheduler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;

import java.util.concurrent.TimeUnit;

public class LogCorrelationKeyTest extends JbpmJUnitBaseTestCase {

	private static final String IT_REDHAT_DEMO = "it/redhat/demo/";

    private RuntimeManager runtimeManager;
    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;

    private CorrelationKeyFactory factory;

    public LogCorrelationKeyTest() {
        super(true, true);
    }

    @Before
    public void before() {

        System.setProperty("drools.clockType", "pseudo");

        runtimeManager = createRuntimeManager(IT_REDHAT_DEMO + "timer-process.bpmn2");
        runtimeEngine = getRuntimeEngine();
        kieSession = runtimeEngine.getKieSession();
        kieSession.getWorkItemManager().registerWorkItemHandler("LogCorrelationKey", new LogCorrelationKey(kieSession));

        factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();

    }

    @After
    public void after() {

        runtimeManager.disposeRuntimeEngine(runtimeEngine);
        runtimeManager.close();

    }
	
	@Test
	public void test() {

        // main process instance
        ProcessInstance pi = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.timer-process", getCorrelationKey(), null);

        assertProcessInstanceActive(pi.getId());
        assertNodeTriggered(pi.getId(), "StartProcess", "LogCorrelationKeyBeforeTimer", "Wait");

        // advance pseudo time
        PseudoClockScheduler sessionClock = kieSession.getSessionClock();
        sessionClock.advanceTime(10, TimeUnit.SECONDS);

        assertProcessInstanceCompleted(pi.getId());
        assertNodeTriggered(pi.getId(), "LogCorrelationKeyAfterTimer", "EndProcess");

    }

    private CorrelationKey getCorrelationKey() {
        return factory.newCorrelationKey("mybusinesskey");
    }

}
