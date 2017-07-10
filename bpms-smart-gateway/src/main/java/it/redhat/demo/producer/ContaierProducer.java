package it.redhat.demo.producer;

import it.redhat.demo.qualifier.ContaierV1;
import it.redhat.demo.qualifier.ContaierV2;
import it.redhat.demo.qualifier.ContaierV3;
import it.redhat.demo.qualifier.ContaierV5;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ReleaseId;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */
@ApplicationScoped
public class ContaierProducer {

    public static final String GROUP_ID = "it.redhat.demo";
    public static final String ARTIFACT_ID = "bpms-selection-process";
    public static final String V_1 = "1.0.0";
    public static final String V_2 = "2.0.0";
    public static final String V_3 = "3.0.0";
    public static final String V_5 = "5.0.0";


    @Produces
    @ContaierV1
    public KieContainerResource produceV1() {
        return produce(V_1);
    }

    @Produces
    @ContaierV2
    public KieContainerResource produceV2() {
        return produce(V_2);
    }

    @Produces
    @ContaierV3
    public KieContainerResource produceV3() {
        return produce(V_3);
    }

    @Produces
    @ContaierV5
    public KieContainerResource produceV5() {
        return produce(V_5);
    }

    private KieContainerResource produce(String version) {

        ReleaseId releaseId = new ReleaseId(GROUP_ID, ARTIFACT_ID, version);
        String gav = GROUP_ID + ":" + ARTIFACT_ID + ":" + version;
        return new KieContainerResource(gav, releaseId);

    }


}
