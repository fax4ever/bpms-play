package it.redhat.demo.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "server-template-key")
public class ServerTemplateKeyDto implements Serializable {
	
	@XmlElement(name = "server-id")
    private String id;
    @XmlElement(name = "server-name")
    private String name;
    
    public ServerTemplateKeyDto() {}
    
	public ServerTemplateKeyDto(String id, String name) {
		this.id = id;
		this.name = name;
	}
    
    

}
