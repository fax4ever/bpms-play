package it.redhat.demo.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.server.controller.api.model.spec.Capability;

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

}
