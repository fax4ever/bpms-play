package it.redhat.demo.bpms.wid;

import java.util.Collections;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestartProcessInstance implements WorkItemHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RestartProcessInstance.class);
	
	private static final String CORRELATION_KEY_VARIABLE_NAME = "dataA";
	private static final String CHECKPOINT_VARIABLE_NAME = "checkpoint";
	
	private final KieSession kieSession;
	private final CheckpointStrategy checkpointStrategy;
	private CorrelationKeyFactory factory;
	
	public RestartProcessInstance(KieSession kieSession, CheckpointStrategy checkpointStrategy) {
		
		this.kieSession = kieSession;
		this.checkpointStrategy = checkpointStrategy;
		factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
		
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		Map<String, Object> parameters = workItem.getParameters();
		String correlationKeyString = (String) parameters.get(CORRELATION_KEY_VARIABLE_NAME);
		CorrelationKey newCorrelationKey = factory.newCorrelationKey(correlationKeyString);
		
		int checkpoint = checkpointStrategy.choose(parameters);
		parameters.put(CHECKPOINT_VARIABLE_NAME, checkpoint);
		
		ProcessInstance newProcessInstance = ((CorrelationAwareProcessRuntime)kieSession).startProcess("it.redhat.demo.bpms.process.checkpoint-start", newCorrelationKey, parameters);
		
		LOG.debug("Restart process instance {}. New process instance {} starts from checkpoint {}", workItem.getId(), newProcessInstance.getId());
		
		manager.completeWorkItem(workItem.getId(), Collections.emptyMap());
		
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
