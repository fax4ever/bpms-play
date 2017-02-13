package it.redhat.demo.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "container-spec-key")
public class ContainerSpecKeyDto implements Serializable {
	
	@XmlElement(name = "container-id")
    private String id;
    @XmlElement(name = "container-name")
    private String containerName;
    @XmlElement(name = "server-template-key")
    private ServerTemplateKeyDto serverTemplateKey;
    
    public ContainerSpecKeyDto() {
    	
    }
    
	public ContainerSpecKeyDto(String id, String containerName, ServerTemplateKeyDto serverTemplateKey) {
		this.id = id;
		this.containerName = containerName;
		this.serverTemplateKey = serverTemplateKey;
	}
    
    

}
