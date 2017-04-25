package it.redhat.demo.stateless;

import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;

import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 25/04/17.
 */
public class SimpleCorrelationKey implements CorrelationKey {

    private static String correlationValue;

    public SimpleCorrelationKey(String correlationValue) {
        this.correlationValue = correlationValue;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<CorrelationProperty<?>> getProperties() {
        return null;
    }

    @Override
    public String toExternalForm() {
        return correlationValue;
    }

}
