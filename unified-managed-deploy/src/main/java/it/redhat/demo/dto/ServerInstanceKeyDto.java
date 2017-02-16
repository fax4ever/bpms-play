package it.redhat.demo.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "server-instance-key")
public class ServerInstanceKeyDto implements Serializable {
	
	@XmlElement(name = "server-instance-id")
    private String serverInstanceId;
    @XmlElement(name = "server-name")
    private String serverName;
    @XmlElement(name = "server-template-id")
    private String serverTemplateId;
    @XmlElement(name = "server-url")
    private String url;
    
    public ServerInstanceKeyDto() {
    	
    }
    
	public ServerInstanceKeyDto(String serverInstanceId, String serverName, String serverTemplateId, String url) {
		this.serverInstanceId = serverInstanceId;
		this.serverName = serverName;
		this.serverTemplateId = serverTemplateId;
		this.url = url;
	}

	public String getServerInstanceId() {
		return serverInstanceId;
	}

	public void setServerInstanceId(String serverInstanceId) {
		this.serverInstanceId = serverInstanceId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerTemplateId() {
		return serverTemplateId;
	}

	public void setServerTemplateId(String serverTemplateId) {
		this.serverTemplateId = serverTemplateId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "ServerInstanceKeyDto [serverInstanceId=" + serverInstanceId + ", serverName=" + serverName
				+ ", serverTemplateId=" + serverTemplateId + ", url=" + url + "]";
	}

}
