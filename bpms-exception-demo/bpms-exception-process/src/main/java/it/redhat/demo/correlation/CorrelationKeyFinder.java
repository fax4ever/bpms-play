package it.redhat.demo.correlation;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.process.CorrelationKey;

/**
 * Created by fabio.ercoli@redhat.com on 15/03/17.
 */
public class CorrelationKeyFinder {

    private boolean perProcessInstance = true;
    private RuntimeManager runtimeManager;

    public CorrelationKeyFinder(boolean perProcessInstance, RuntimeManager runtimeManager) {
        this.perProcessInstance = perProcessInstance;
        this.runtimeManager = runtimeManager;
    }

    public CorrelationKeyFinder(boolean perProcessInstance) {
        this.perProcessInstance = perProcessInstance;
    }

    public String findCorrelationKey(ProcessEvent event) {

        return (perProcessInstance) ? findCorrelationKeyPis(event) : findCorrelationKeyNotPis(event);

    }

    private String findCorrelationKeyNotPis(ProcessEvent event) {

        // first of all we need to find the root process instance
        ProcessInstanceImpl rootPi = ProcessInstanceHelper.findRoot((ProcessInstanceImpl) event.getProcessInstance(), (KieSession) event.getKieRuntime());

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

    private String findCorrelationKeyPis(ProcessEvent event) {

        // first of all we need to find the root process instance
        ProcessInstanceImpl rootPi = ProcessInstanceHelper.findRoot((ProcessInstanceImpl) event.getProcessInstance(), runtimeManager);

        CorrelationKey correlationKey = (CorrelationKey) rootPi.getMetaData().get("CorrelationKey");
        if (correlationKey != null) {
            return correlationKey.toExternalForm();
        }

        ProcessInstanceLog rootPiLog = (ProcessInstanceLog) ProcessInstanceHelper.getAuditPerProcessInstance(rootPi.getId(), runtimeManager);
        if (rootPiLog == null) {
            return null;
        }

        return rootPiLog.getCorrelationKey();

    }

    public void setRuntimeManager(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }

}
