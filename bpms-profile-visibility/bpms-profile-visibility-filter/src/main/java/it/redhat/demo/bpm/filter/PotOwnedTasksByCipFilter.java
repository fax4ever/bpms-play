package it.redhat.demo.bpm.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.services.api.query.QueryParamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.dashbuilder.dataset.filter.FilterFactory.AND;
import static org.dashbuilder.dataset.filter.FilterFactory.NOT;
import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.in;
import static org.dashbuilder.dataset.filter.FilterFactory.isNull;

public class PotOwnedTasksByCipFilter implements QueryParamBuilder<ColumnFilter> {

    private static final Logger LOG = LoggerFactory.getLogger(PotOwnedTasksByCipFilter.class);

    private static final String[] ACTIVE_STATUS = {"Created", "Ready", "Reserved", "InProgress", "Suspended"};

    private Map<String, Object> parameters;
    private boolean built = false;

    public PotOwnedTasksByCipFilter(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public ColumnFilter build() {

        if (built) {
            return null;
        }

        String user = (String) parameters.get("user");
        List<String> groups = (List<String>) parameters.get("groups");
        List<String> status = Arrays.asList(ACTIVE_STATUS);
        String cip = (String) parameters.get("cip");

        LOG.debug("user: {}", user);
        LOG.debug("groups: {}", groups);
        LOG.debug("status: {}", status);
        LOG.debug("cip: {}", cip);

        // base filter based on common pot owner criteria
        ColumnFilter filter = AND(
            OR(equalsTo("actualOwner", user), equalsTo("actualOwner", "")),
            OR(equalsTo("potOwner", user), in("potOwner", groups)),
            OR(isNull("exclOwner"), NOT(equalsTo("exclOwner", user))),
            equalsTo("cip", cip)
        );

        built = true;

        LOG.debug("filter instance: {}", filter);
        return filter;
    }

    private ColumnFilter buildMapFilter(Map<String, List<String>> map, String name, String value) {

        return OR(map.entrySet().stream()
            .map(entry -> AND(equalsTo(name, entry.getKey()), in(value, entry.getValue()))
        ).toArray(ColumnFilter[]::new));

    }

}
