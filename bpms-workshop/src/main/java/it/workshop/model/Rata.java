package it.workshop.model;

import java.io.Serializable;
import java.util.Date;

public class Rata implements Serializable {

	private boolean last;
	private Integer amount;
	private Long dueDate;

	public boolean isLast() {
		return last;
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

	public Long getDueDate() {
		return dueDate;
	}

	public void setDueDate(Long dueDate) {
		this.dueDate = dueDate;
	}

	public String getTimeToWait() {
		
		long now = new Date().getTime();
		long millisToWait = dueDate - now;
		long secondToWait = millisToWait / 1000;
		
		return secondToWait + "s";
		
	}

}
