package it.redhat.demo.bpm.process.query;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.bpm.process.exception.SignalNotAvilableException;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

public class SignalService {

	private static final Logger LOG = LoggerFactory.getLogger(SignalService.class);

	public static final String CONTAINER_ID = "it.redhat.demo:bpms-invoke-signal-process:1.0.0-SNAPSHOT";

	private final KieServicesConfiguration config;

	private KieServicesClient client;

	public SignalService(GatewaySettings settings) {

		String serverUrl = new StringBuilder(settings.getProtocol()).append("://").append(settings.getHostname())
				.append(":").append(settings.getPort()).append("/").append(settings.getContextPath())
				.append("/services/rest/server").toString();

		LOG.info("Server Url {}", serverUrl);

		config = KieServicesFactory.newRestConfiguration(serverUrl, settings.getUsername(), settings.getPassword());
		config.setMarshallingFormat(MarshallingFormat.JSON);
		config.setTimeout(settings.getTimeout());

	}
	
	public void startConverasation() {
        client = KieServicesFactory.newKieServicesClient(config);
    }
	
	public void endConversation() {

        client.completeConversation();

    }
	
	public Long startProcess() {

        return client.getServicesClient(ProcessServicesClient.class).startProcess(CONTAINER_ID, "it.redhat.demo.bpm.process.signal", new HashMap<>());

    }
	
	public void sendSignalSafe(long processInstanceId, String signalName, Object signalContent) {
		
		List<String> availableSignals = client.getServicesClient(ProcessServicesClient.class).getAvailableSignals(CONTAINER_ID, processInstanceId);
		if (!availableSignals.contains(signalName)) {
			throw new SignalNotAvilableException(processInstanceId, signalName);
		}
		
		client.getServicesClient(ProcessServicesClient.class).signalProcessInstance(CONTAINER_ID, processInstanceId, signalName, signalContent);
		
	}

	public ProcessInstance getProcessInstance(long processInstanceId) {

		return client.getServicesClient( QueryServicesClient.class ).findProcessInstanceById( processInstanceId );

	}

}
