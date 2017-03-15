package it.redhat.demo.listener;

import it.redhat.demo.correlation.CorrelationKeyFinder;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 15/03/17.
 */
public class LogProcessEventListener extends DefaultProcessEventListener {

    private static final Logger log = LoggerFactory.getLogger(LogProcessEventListener.class);

    // local cache for correlation keys
    // key is the process instance ID
    // value is the correlation key
    // this map will be a single item map if the strategy is per process instance
    // volatile is useful only with singleton strategy
    // in the other strategies the listener object will not be shared with other threads
    private volatile Map<Long, String> correlationKeys = new HashMap<>();

    private CorrelationKeyFinder finder;

    public LogProcessEventListener(RuntimeManager runtimeManager, boolean perProcessInstanceStrategy) {
        finder = new CorrelationKeyFinder(perProcessInstanceStrategy, runtimeManager);
    }

    public LogProcessEventListener(boolean perProcessInstanceStrategy) {
        finder = new CorrelationKeyFinder(perProcessInstanceStrategy);
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        
        Object newValue = event.getNewValue();
        // skip logging if the new value is null or an empty String
        if (newValue == null || newValue.toString().trim().isEmpty()) {
            return;
        }

        Object oldValue = event.getOldValue();
        String variableId = event.getVariableId();
        ProcessInstance pi = event.getProcessInstance();
        String processId = pi.getProcessId();

        // using for probing object instance scope
        log.trace("Listener Identity Object Instance: [{}]", System.identityHashCode(this));

        if (!correlationKeys.containsKey(pi.getId())) {
            log.trace("search correlation key for process instance {}", pi.getId());
            String correlationKeyPis = finder.findCorrelationKey(event);

            if (correlationKeyPis != null) {
                correlationKeys.put(pi.getId(), correlationKeyPis);
            }

        } else {
            log.trace("cache correlation key for process instance {}", pi.getId());
        }

        String correlationKey = correlationKeys.get(pi.getId());
        // skip logging if it is not possible to associate a correlation key
        if (correlationKey == null) {
            return;
        }

        log.info("correlationKey: [{}], processId: [{}], pi: [{}], parent: [{}], variable: [{}], oldValue [{}], newValue: [{}]",
                correlationKey, processId, pi.getId(), pi.getParentProcessInstanceId(), variableId, oldValue, newValue);


    }

    public void setRuntimeManager(RuntimeManager runtimeManager) {
        this.finder.setRuntimeManager(runtimeManager);
    }

}
