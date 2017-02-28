package it.redhat.demo.model;

import java.io.Serializable;

public class Command implements Serializable {

	private static final long serialVersionUID = -3193391223141449505L;

	private String name;
	private Integer value;
	private Boolean option;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Boolean getOption() {
		return option;
	}

	public void setOption(Boolean option) {
		this.option = option;
	}

	@Override
	public String toString() {
		return "Command [name=" + name + ", value=" + value + ", option=" + option + "]";
	}

}
