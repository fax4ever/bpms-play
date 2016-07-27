package it.redhat.demo.rest;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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

        ksession.startProcess("example.sayGoodbye");

    }

}
