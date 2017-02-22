package it.redhat.demo.pojo;

import java.io.Serializable;
import java.util.Date;

public class KieMessage implements Serializable {

	private String content;
	private String severity;
	private Date timestamp;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "KieMessage [content=" + content + ", severity=" + severity + ", timestamp=" + timestamp + "]";
	}

}
