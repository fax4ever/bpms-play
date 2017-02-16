package it.redhat.demo.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import it.redhat.demo.dto.ReleaseIdDto;

@XmlRootElement(name = "kie-container")
@XmlAccessorType(XmlAccessType.FIELD)
public class KieContainerResourcePojo implements Serializable {

	@XmlAttribute
	private String containerId;
	
	@XmlAttribute
	private String status;
	
	@XmlElement(name = "config-items")
	private List<ConfigItemPojo> configs = new ArrayList<>();
	
	@XmlElement(name = "messages")
	private List<KieMessage> messages = new ArrayList<>();
	
	@XmlElement(name = "release-id")
	private ReleaseIdDto releaseId;
	
	@XmlElement(name = "resolved-release-id")
	private ReleaseIdDto resolvedReleaseId;
	
	@XmlElement(name = "scanner")
	private ScannerPojo scanner;

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ConfigItemPojo> getConfigs() {
		return configs;
	}

	public void setConfigs(List<ConfigItemPojo> configs) {
		this.configs = configs;
	}

	public List<KieMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<KieMessage> messages) {
		this.messages = messages;
	}

	public ReleaseIdDto getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(ReleaseIdDto releaseId) {
		this.releaseId = releaseId;
	}

	public ReleaseIdDto getResolvedReleaseId() {
		return resolvedReleaseId;
	}

	public void setResolvedReleaseId(ReleaseIdDto resolvedReleaseId) {
		this.resolvedReleaseId = resolvedReleaseId;
	}

	public ScannerPojo getScanner() {
		return scanner;
	}

	public void setScanner(ScannerPojo scanner) {
		this.scanner = scanner;
	}

	@Override
	public String toString() {
		return "KieContainerResourcePojo [containerId=" + containerId + ", status=" + status + ", configs=" + configs
				+ ", messages=" + messages + ", releaseId=" + releaseId + ", resolvedReleaseId=" + resolvedReleaseId
				+ ", scanner=" + scanner + "]";
	}
	
}
