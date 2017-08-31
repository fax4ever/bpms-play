package it.redhat.demo.bpms.wid;

import java.util.Map;

public interface CheckpointStrategy {
	
	int choose(Map<String, Object> variables);

}
