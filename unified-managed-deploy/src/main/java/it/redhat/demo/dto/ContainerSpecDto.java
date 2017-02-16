package it.redhat.demo.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.spec.Capability;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "container-spec-details")
public class ContainerSpecDto extends ContainerSpecKeyDto implements Serializable {
	
	@XmlElement(name = "release-id")
    private ReleaseIdDto releasedId;
    @XmlElement(name = "configuration", type = ProcessConfigDto.class)
    private Map<Capability, ProcessConfigDto> configs = new HashMap<Capability, ProcessConfigDto>();
    @XmlElement(name = "status")
    private KieContainerStatus status = KieContainerStatus.STOPPED;
    
    public ContainerSpecDto() {
    	
    }
    
	public ContainerSpecDto(String id, String containerName, ServerTemplateKeyDto serverTemplateKey,
			ReleaseIdDto releaseId, KieContainerStatus status, Map<Capability, ProcessConfigDto> config) {
    	super(id, containerName, serverTemplateKey);
    	this.releasedId = releaseId;
    	this.status = status;
    	this.configs = config;
	}
	
	public void addConfig(Capability capability, ProcessConfigDto config) {
        this.configs.put(capability, config);
    }

	public ReleaseIdDto getReleasedId() {
		return releasedId;
	}

	public void setReleasedId(ReleaseIdDto releasedId) {
		this.releasedId = releasedId;
	}

	public Map<Capability, ProcessConfigDto> getConfigs() {
		return configs;
	}

	public void setConfigs(Map<Capability, ProcessConfigDto> configs) {
		this.configs = configs;
	}

	public KieContainerStatus getStatus() {
		return status;
	}

	public void setStatus(KieContainerStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ContainerSpecDto [releasedId=" + releasedId + ", configs=" + configs + ", status=" + status + "]";
	}

}
