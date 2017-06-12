package it.redhat.demo.correlation;

import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;

import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 12/06/2017.
 */
public class StringCorrelationKey implements CorrelationKey {

    private static String correlationValue;

    public StringCorrelationKey(String correlationValue) {
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
