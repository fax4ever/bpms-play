package it.workshop.model;

import java.io.Serializable;
import java.util.Date;

public class Rata implements Serializable {

	private Boolean last;
	private Integer amount;

	public Rata() {}

	public Rata(boolean last, Integer amount) {
		this.last = last;
		this.amount = amount;
	}

	public Boolean getLast() {
		return last;
	}

	public void setLast(Boolean last) {
		this.last = last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

}
