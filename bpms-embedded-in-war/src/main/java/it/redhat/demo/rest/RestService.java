package it.redhat.demo.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * @author Fabio Massimo Ercoli
 *         fabio.ercoli@redhat.com
 *         on 27/07/16
 */
@Path("")
public class RestService {

    @POST
    public void startProcess() {

        KieServices kservices = KieServices.Factory.get();
        KieContainer kcontainer = kservices.getKieClasspathContainer();
        KieBase kbase = kcontainer.getKieBase();
        KieSession ksession = kbase.newKieSession();

        ksession.startProcess("it.redhat.demo.simple");

    }

}
