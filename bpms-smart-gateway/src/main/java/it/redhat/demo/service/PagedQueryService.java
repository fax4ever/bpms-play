package it.redhat.demo.service;

import org.kie.server.client.QueryServicesClient;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 04/07/2017.
 */

@Stateless
public class PagedQueryService {

    @Inject
    private QueryServicesClient queryServices;

    public void potOwnedTasksByVariablesAndParams(String user, List<String> groups, List<String> status, Map<String, List<String>> paramsMap, Map<String, List<String>> variablesMap, Integer page, Integer pageSize) {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("groups", groups);

        if (status != null) {
            parameters.put("status", status);
        }

        if (paramsMap != null) {
            parameters.put("paramsMap", paramsMap);
        }

        if (variablesMap != null) {
            parameters.put("variablesMap", variablesMap);
        }






    }

}
