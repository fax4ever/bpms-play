package it.redhat.demo.bpms.model;

public class VariablesWrapper {
	
	private final String dataA;
	private final String dataB;
	private final String dataC;
	
	public VariablesWrapper(String dataA, String dataB, String dataC) {
		this.dataA = dataA;
		this.dataB = dataB;
		this.dataC = dataC;
	}

	public String getDataA() {
		return dataA;
	}

	public String getDataB() {
		return dataB;
	}

	public String getDataC() {
		return dataC;
	}
	
}
