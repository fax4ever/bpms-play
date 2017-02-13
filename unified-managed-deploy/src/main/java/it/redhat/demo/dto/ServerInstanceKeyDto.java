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
    
    
   

}
