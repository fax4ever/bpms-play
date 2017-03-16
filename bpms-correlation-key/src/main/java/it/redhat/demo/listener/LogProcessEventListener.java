package it.redhat.demo.listener;

import it.redhat.demo.correlation.CorrelationKeyFinder;
import org.kie.api.event.process.*;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by fabio.ercoli@redhat.com on 15/03/17.
 */
public class LogProcessEventListener extends DefaultProcessEventListener {

    private static final Logger log = LoggerFactory.getLogger(LogProcessEventListener.class);
    private static final String[] SKIP_CHANGE_ON_THIS_VARIABLES = { "processId", "initiator" };

    // local cache for correlation keys
    // key is the process instance ID
    // value is the correlation key
    // this map will be a single item map if the strategy is per process instance
    // volatile is useful only with singleton strategy
    // in the other strategies the listener object will not be shared with other threads
    private volatile Map<Long, String> correlationKeys = new HashMap<>();

    private final CorrelationKeyFinder finder;
    private final Set<String> skippedVariableSet = new HashSet<>(Arrays.asList(SKIP_CHANGE_ON_THIS_VARIABLES));

    public LogProcessEventListener(RuntimeManager runtimeManager, boolean perProcessInstanceStrategy) {
        finder = new CorrelationKeyFinder(perProcessInstanceStrategy, runtimeManager);
    }

    public LogProcessEventListener(boolean perProcessInstanceStrategy) {
        finder = new CorrelationKeyFinder(perProcessInstanceStrategy);
    }

    public void setRuntimeManager(RuntimeManager runtimeManager) {
        this.finder.setRuntimeManager(runtimeManager);
    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {

        verifyAndFillCKMap(event);

        ProcessInstance pi = event.getProcessInstance();
        String processId = pi.getProcessId();
        String correlationKey = correlationKeys.get(pi.getId());

        log.info("Start Process - correlationKey: [{}], processId: [{}], pi: [{}], parent: [{}]", correlationKey, processId, pi.getId(), pi.getParentProcessInstanceId());

    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {

        verifyAndFillCKMap(event);

        ProcessInstance pi = event.getProcessInstance();
        String processId = pi.getProcessId();
        String correlationKey = correlationKeys.get(pi.getId());

        log.info("End Process - correlationKey: [{}], processId: [{}], pi: [{}], parent: [{}]", correlationKey, processId, pi.getId(), pi.getParentProcessInstanceId());

    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        
        Object newValue = event.getNewValue();
        String variableId = event.getVariableId();

        // skip logging if the new value is null or an empty String
        if (newValue == null || newValue.toString().trim().isEmpty() || skippedVariableSet.contains(variableId)) {
            return;
        }

        Object oldValue = event.getOldValue();
        ProcessInstance pi = event.getProcessInstance();
        String processId = pi.getProcessId();

        verifyAndFillCKMap(event);

        String correlationKey = correlationKeys.get(pi.getId());
        // skip logging if it is not possible to associate a correlation key
        if (correlationKey == null) {
            return;
        }

        log.info("Change Variable - correlationKey: [{}], processId: [{}], pi: [{}], parent: [{}], variable: [{}], oldValue [{}], newValue: [{}]",
                correlationKey, processId, pi.getId(), pi.getParentProcessInstanceId(), variableId, oldValue, newValue);


    }

    private void verifyAndFillCKMap(ProcessEvent event) {
        long piid = event.getProcessInstance().getId();

        if (!correlationKeys.containsKey(piid)) {
            log.trace("search correlation key for process instance {}", piid);
            String correlationKeyPis = finder.findCorrelationKey(event);

            if (correlationKeyPis != null) {
                correlationKeys.put(piid, correlationKeyPis);
            }

        } else {
            log.trace("cache correlation key for process instance {}", piid);
        }
    }

}
