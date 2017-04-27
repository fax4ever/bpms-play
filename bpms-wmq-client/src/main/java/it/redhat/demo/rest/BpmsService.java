package it.redhat.demo.rest;

import it.redhat.demo.jms.BpmsJmsGateway;
import it.redhat.demo.jms.producer.SendSignal;
import it.redhat.demo.jms.producer.StartProcess;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by fabio.ercoli@redhat.com on 27/04/17.
 */

@Stateless
public class BpmsService {

    @Inject
    @StartProcess
    private String startProcessTemplate;

    @Inject
    @SendSignal
    private String sendSignalTemplate;

    @Inject
    private BpmsJmsGateway gateway;

    public void startProcess(String container, String definition, String payload) {

        String jsonCommand = startProcessTemplate
            .replace("{{container}}", container)
            .replace("{{definition}}", definition)
            .replace("{{payload}}", payload)
            .replace("{{correlationKey}}", UUID.randomUUID().toString());

        gateway.send(container, jsonCommand);

    }

    public void sendSignal(String container, Long processInstance, String name, String value) {

        String jsonCommand = sendSignalTemplate
                .replace("{{container}}", container)
                .replace("{{processInstance}}", processInstance.toString())
                .replace("{{name}}", name)
                .replace("{{payload}}", value);

        gateway.send(container, jsonCommand);

    }

}
