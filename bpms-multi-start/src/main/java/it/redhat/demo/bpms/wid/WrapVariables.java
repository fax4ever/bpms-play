package it.redhat.demo.bpms.wid;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import it.redhat.demo.bpms.model.VariablesWrapper;

public class WrapVariables implements WorkItemHandler {
	
	public static final String VARIABLES_WRAPPER_VARIABLE_NAME = "variablesWrapper";
	public static final String CHECKPOINT_VARIABLE_NAME = "checkpoint";
	
	private final CheckpointStrategy checkpointStrategy;

	public WrapVariables(CheckpointStrategy checkpointStrategy) {
		this.checkpointStrategy = checkpointStrategy;
	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		Map<String, Object> parameters = workItem.getParameters();
		
		parameters.remove(VARIABLES_WRAPPER_VARIABLE_NAME);
		Integer checkpoint = (Integer) parameters.remove(CHECKPOINT_VARIABLE_NAME);
		
		// checkpoint zero means that the checkpoint entry point will be choose by the process itself
		if (checkpoint <= 0) {
			checkpoint = checkpointStrategy.choose(parameters);
		}
		
		Map<String, Object> results = new HashMap<String, Object>();
		results.put(VARIABLES_WRAPPER_VARIABLE_NAME, new VariablesWrapper(parameters));
		results.put(CHECKPOINT_VARIABLE_NAME, checkpoint);
		
		manager.completeWorkItem(workItem.getId(), results);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
	}

}
