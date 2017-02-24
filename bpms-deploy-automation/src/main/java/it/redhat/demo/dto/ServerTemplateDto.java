package it.redhat.demo.dto;

import org.kie.server.controller.api.model.spec.Capability;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "server-template-details")
public class ServerTemplateDto extends ServerTemplateKeyDto implements Serializable {
	
	
	@XmlElement(name = "container-specs")
    private Collection<ContainerSpecDto> containersSpec = new ArrayList<>();
    @XmlElement(name = "server-config")
    private Map<Capability, ServerConfigDto> configs = new HashMap<>();
    @XmlElement(name = "server-instances")
    private Collection<ServerInstanceKeyDto> serverInstances = new ArrayList<>();
    @XmlElement(name="capabilities")
    private List<String> capabilities = new ArrayList<String>();
    
    public ServerTemplateDto() {}
    
    public ServerTemplateDto(String serverId, String serverName) {
		super(serverId, serverName);
	}

	public void addServerInstance(ServerInstanceKeyDto serverInstance) {
		if (!serverInstances.contains(serverInstance)) {
            serverInstances.add(serverInstance);
        }
	}

	public void addContainerSpec(ContainerSpecDto containerSpec) {
		containersSpec.add(containerSpec);
	}

	public Collection<ContainerSpecDto> getContainersSpec() {
		return containersSpec;
	}

	public void setContainersSpec(Collection<ContainerSpecDto> containersSpec) {
		this.containersSpec = containersSpec;
	}

	public Map<Capability, ServerConfigDto> getConfigs() {
		return configs;
	}

	public void setConfigs(Map<Capability, ServerConfigDto> configs) {
		this.configs = configs;
	}

	public Collection<ServerInstanceKeyDto> getServerInstances() {
		return serverInstances;
	}

	public void setServerInstances(Collection<ServerInstanceKeyDto> serverInstances) {
		this.serverInstances = serverInstances;
	}

	public List<String> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(List<String> capabilities) {
		this.capabilities = capabilities;
	}

	public Collection<ServerInstanceKeyDto> getServerInstanceKeys() {
		 return serverInstances;
	}

	@Override
	public String toString() {
		return "ServerTemplateDto [containersSpec=" + containersSpec + ", configs=" + configs + ", serverInstances="
				+ serverInstances + ", capabilities=" + capabilities + "]";
	}

}
