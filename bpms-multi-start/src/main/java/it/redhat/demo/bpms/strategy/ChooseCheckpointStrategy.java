package it.redhat.demo.bpms.strategy;

import java.util.Map;

import it.redhat.demo.bpms.wid.CheckpointStrategy;

public class ChooseCheckpointStrategy implements CheckpointStrategy {

	@Override
	public int choose(Map<String, Object> variables) {
		// put here the algorithm to choose the checkpoint
		// every process will be its own strategy
		if (variables.get("dataC") != null) {
			return 2;
		} else {
			return 1;
		}
	}

}
