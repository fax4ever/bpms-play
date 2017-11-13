/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package it.redhat.demo.bpm.process.query;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static org.kie.server.client.QueryServicesClient.QUERY_MAP_TASK;

/**
 * @author Fabio Massimo Ercoli (C) 2017 Red Hat Inc.
 */
public class AdvancedQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(AdvancedQueryService.class);

    public static final String QUERY_NAME = "potOwnedByCip";
    public static final String FILTER_NAME = "potOwnedByCip";
    public static final String CONTAINER_ID = "it.redhat.demo.bpm.solution:profile-visibility-process:0.0.1-SNAPSHOT";

    private final String datasource;
    private final KieServicesConfiguration config;

    private KieServicesClient client;

    public AdvancedQueryService(GatewaySettings settings, String datasource) {

        String serverUrl = new StringBuilder(settings.getProtocol())
                .append("://").append(settings.getHostname())
                .append(":").append(settings.getPort())
                .append("/").append(settings.getContextPath())
                .append("/services/rest/server")
                .toString();

        LOG.info("Server Url {}", serverUrl);

        config = KieServicesFactory.newRestConfiguration(serverUrl, settings.getUsername(), settings.getPassword());
        config.setMarshallingFormat(MarshallingFormat.JSON);
        config.setTimeout(settings.getTimeout());

        this.datasource = datasource;

    }

    public void startConverasation() {
        client = KieServicesFactory.newKieServicesClient(config);
    }

    public void registerAdvancedQuery() {

        QueryDefinition query = new QueryDefinition();
        query.setName(QUERY_NAME);
        query.setSource(datasource);
        query.setExpression(" select task.*, pot.entity_id potowner, ex.entity_id exclowner, own.cip cip " +
                " from audittaskimpl task " +
                " inner join peopleassignments_potowners pot " +
                " on (pot.task_id = task.taskid) " +
                " left join peopleassignments_exclowners ex " +
                " on (ex.task_id = task.taskid)" +
                " inner join (select max(mv.map_var_id) as map_var_id, mv.processinstanceid from MappedVariable mv group by mv.processinstanceid order by mv.processinstanceid) mv " +
                " on (mv.processInstanceId = task.processInstanceId) " +
                " inner join Ownership own " +
                " on (own.id = mv.map_var_id) ");
        query.setTarget("CUSTOM");

        client.getServicesClient(QueryServicesClient.class).replaceQuery(query);

    }

    public List<TaskInstance> potOwnedByCip(String user, List<String> groups, String cip, int page, int pageSize) {

        HashMap<String, Object> parameters = new HashMap<>();

        parameters.put("user", user);
        parameters.put("groups", groups);
        parameters.put("cip", cip);

        LOG.info("Query parameters: {}", parameters);

        List<TaskInstance> query = client.getServicesClient(QueryServicesClient.class)
                .query(QUERY_NAME, QUERY_MAP_TASK, FILTER_NAME, parameters, page, pageSize, TaskInstance.class);

        return query;

    }

    public void endConversation() {

        client.completeConversation();

    }

    public void changeCip(long pi, String cip) {

        client.getServicesClient(ProcessServicesClient.class).signalProcessInstance(CONTAINER_ID, pi, "changeCIP", cip);

    }

    public Long startProcess(String cip, String agency) {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("cip", cip);
        parameters.put("agency", agency);

        return client.getServicesClient(ProcessServicesClient.class).startProcess(CONTAINER_ID, "it.redhat.demo.bpm.process.task-ownership", parameters);

    }

}
