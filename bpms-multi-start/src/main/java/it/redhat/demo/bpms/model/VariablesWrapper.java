package it.redhat.demo.bpms.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Process Instance Variables Immutable Wrapper
 * 
 * @author fabio
 *
 */
public final class VariablesWrapper implements Serializable {
	
	private static final long serialVersionUID = 4992936447883433384L;
	private final Map<String, Object> variables;
	
	public VariablesWrapper(Map<String, Object> parameters) {
		this.variables = parameters;
	}
	
	public Map<String, Object> getParameters() {
		return variables;
	}

	@Override
	public String toString() {
		return "VariablesWrapper [variables=" + variables + "]";
	}
	
}
