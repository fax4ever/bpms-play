package it.redhat.demo.correlation;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.manager.context.EmptyContext;

/**
 * Created by fabio.ercoli@redhat.com on 15/03/17.
 */
public class CorrelationKeyTaskFinder {

    private boolean perProcessInstance = true;
    private RuntimeManager runtimeManager;

    public CorrelationKeyTaskFinder(boolean perProcessInstance, RuntimeManager runtimeManager) {
        this.perProcessInstance = perProcessInstance;
        this.runtimeManager = runtimeManager;
    }

    public CorrelationKeyTaskFinder(boolean perProcessInstance) {
        this.perProcessInstance = perProcessInstance;
    }

    public String findCorrelationKey(Long processInstanceId) {

        return (perProcessInstance) ? findCorrelationKeyPis(processInstanceId) : findCorrelationKeyNotPis(processInstanceId);

    }

    private String findCorrelationKeyNotPis(Long processInstanceId) {

        RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get());
        KieSession kieSession = runtimeEngine.getKieSession();

        ProcessInstanceImpl processInstance = (ProcessInstanceImpl) kieSession.getProcessInstance(processInstanceId);

        // first of all we need to find the root process instance
        ProcessInstanceImpl rootPi = ProcessInstanceHelper.findRoot((ProcessInstanceImpl) processInstance, kieSession);

        CorrelationKey correlationKey = (CorrelationKey) rootPi.getMetaData().get("CorrelationKey");
        if (correlationKey != null) {
            return correlationKey.toExternalForm();
        }

        ProcessInstanceLog rootPiLog = (ProcessInstanceLog) ProcessInstanceHelper.getAuditNotPerProcessInstance(rootPi.getId(), runtimeManager);
        if (rootPiLog == null) {
            return null;
        }

        return rootPiLog.getCorrelationKey();

    }

    private String findCorrelationKeyPis(Long processInstanceId) {

        ProcessInstanceLog rootPiLog = (ProcessInstanceLog) ProcessInstanceHelper.getRootAuditPerProcessInstance(processInstanceId, runtimeManager);
        if (rootPiLog == null) {
            return null;
        }

        return rootPiLog.getCorrelationKey();

    }

    public void setRuntimeManager(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }

}
