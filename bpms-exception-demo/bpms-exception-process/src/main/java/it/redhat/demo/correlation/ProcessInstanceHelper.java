package it.redhat.demo.correlation;

import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.internal.runtime.manager.SessionNotFoundException;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fabio.ercoli@redhat.com on 15/03/17.
 */
public class ProcessInstanceHelper {

    private static final Logger log = LoggerFactory.getLogger(ProcessInstanceHelper.class);

    /**
     * Given a process instance find its root process instance.
     * If the process instance doesn't have a parent process instance is a root of itself.
     *
     * This method is applicable only for SINGLETON and PER_REQUEST strategy,
     * where all related process instances are stored in the same kie session.
     *
     * @param pi              target process instance
     * @param kieSession      the kie session containing all process instance hierarchy
     * @return                root hierarchy process instance
     */
    public static ProcessInstanceImpl findRoot(ProcessInstanceImpl pi, KieSession kieSession) {

        long parentProcessInstanceId = pi.getParentProcessInstanceId();
        if (parentProcessInstanceId <= 0) {
            return pi;
        }

        ProcessInstanceImpl parentProcessInstance = (ProcessInstanceImpl) kieSession.getProcessInstance(parentProcessInstanceId);
        return ProcessInstanceHelper.findRoot(parentProcessInstance, kieSession);

    }

    /**
     * Given a process instance find its root process instance.
     * If the process instance doesn't have a parent process instance is a root of itself.
     *
     * This method is applicable only for PER_PROCESS_INSTANCE strategy,
     * where different process instances are always stored in different kie session.
     *
     * @param pi              target process instance
     * @param runtimeManager  the runtimeManager containing all kie session
     * @return                root hierarchy process instance
     */
    public static ProcessInstanceImpl findRoot(ProcessInstanceImpl pi, RuntimeManager runtimeManager) {

        long parentProcessInstanceId = pi.getParentProcessInstanceId();
        if (parentProcessInstanceId <= 0) {
            return pi;
        }

        // use the runtime manager to extract the runtime engine relative to the parent process instance
        RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(parentProcessInstanceId));
        KieSession kieSession = runtimeEngine.getKieSession();

        ProcessInstanceImpl parentProcessInstance = (ProcessInstanceImpl) kieSession.getProcessInstance(parentProcessInstanceId);
        return ProcessInstanceHelper.findRoot(parentProcessInstance, kieSession);

    }

    /**
     * Given a process instance find the relative audit instance, if it exists.
     *
     * This method is applicable only for PER_PROCESS_INSTANCE strategy,
     * where different process instances are always stored in different kie session.
     *
     * @param processInstanceId     process instance id
     * @param runtimeManager        global runtime manager
     * @return                      the audit process instance if it exists, otherwise null
     */
    public static ProcessInstanceLog getAuditPerProcessInstance(Long processInstanceId, RuntimeManager runtimeManager) {

        try {

            RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
            AuditService auditService = runtimeEngine.getAuditService();

            return auditService.findProcessInstance(processInstanceId);

        } catch (SessionNotFoundException ex) {

            log.trace("audit process instance {} does not exist", processInstanceId);
            return null;

        }

    }

    public static ProcessInstanceLog getRootAuditPerProcessInstance(Long processInstanceId, RuntimeManager runtimeManager) {

        ProcessInstanceLog auditPerProcessInstance = ProcessInstanceHelper.getAuditPerProcessInstance(processInstanceId, runtimeManager);

        if (auditPerProcessInstance == null) {

            log.trace("audit process instance with pid {} not found", auditPerProcessInstance);
            return null;
        }

        if (auditPerProcessInstance.getParentProcessInstanceId() <= 0) {
            return auditPerProcessInstance;
        }

        return getRootAuditPerProcessInstance(auditPerProcessInstance.getParentProcessInstanceId(), runtimeManager);

    }

    /**
     * Given a process instance find the relative audit instance, if it exists.
     *
     * This method is applicable only for SINGLETON and PER_REQUEST strategy,
     * where all related process instances are stored in the same kie session.
     *
     * @param processInstanceId     process instance id
     * @param runtimeManager        global runtime manager
     * @return                      the audit process instance if it exists, otherwise null
     */
    public static ProcessInstanceLog getAuditNotPerProcessInstance(Long processInstanceId, RuntimeManager runtimeManager) {

        try {

            RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get());
            AuditService auditService = runtimeEngine.getAuditService();

            return auditService.findProcessInstance(processInstanceId);

        } catch (SessionNotFoundException ex) {

            log.trace("audit process instance {} does not exist", processInstanceId);
            return null;

        }

    }

}
