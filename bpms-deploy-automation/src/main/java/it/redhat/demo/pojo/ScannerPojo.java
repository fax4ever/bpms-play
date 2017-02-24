package it.redhat.demo.pojo;

import java.io.Serializable;

public class ScannerPojo implements Serializable {

	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ScannerPojo [status=" + status + "]";
	}
		
}


