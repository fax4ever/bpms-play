package it.redhat.demo;

import java.io.Serializable;

public class Person implements Serializable {

	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
