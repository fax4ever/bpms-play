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
package it.redhat.demo.bpm.process.main;

import org.kie.server.api.model.instance.TaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.redhat.demo.bpm.process.query.AdvancedQueryService;
import it.redhat.demo.bpm.process.query.GatewaySettings;

import java.util.Collections;
import java.util.List;

/**
 * @author Fabio Massimo Ercoli (C) 2017 Red Hat Inc.
 */
public class AdvancedQueryMain {

    private static final Logger LOG = LoggerFactory.getLogger(AdvancedQueryMain.class);

    public static void main(String[] args) {
    	
    	//TODO change these values
        String username = "fabio";
        String password = "fabio$739";
        String datasource = "java:/jbpm";

        AdvancedQueryService advancedQueryService = new AdvancedQueryService(GatewaySettings.create(username, password), datasource);
        advancedQueryService.startConverasation();

        Long pid = advancedQueryService.startProcess("A007", "123456");

        // registration is needed only once
        // if the query was already defined the definition would be override
        advancedQueryService.registerAdvancedQuery();

        // queries
        List<TaskInstance> result = advancedQueryService.potOwnedByCip("marco", Collections.singletonList("INSURANCE_AGENT_ROLE_123456"), "A007", 0, 10);
        LOG.info("Query result: {}", result);

        result = advancedQueryService.potOwnedByCip("diego", Collections.singletonList("INSURANCE_AGENT_ROLE_123456"), "B003", 0, 10);
        LOG.info("Query result: {}", result);

        result = advancedQueryService.potOwnedByCip("dario", Collections.singletonList("INSURANCE_AGENT_ROLE_234567"), "B003", 0, 10);
        LOG.info("Query result: {}", result);

        advancedQueryService.changeCip(pid, "B003");

        // queries
        result = advancedQueryService.potOwnedByCip("marco", Collections.singletonList("INSURANCE_AGENT_ROLE_123456"), "A007", 0, 10);
        LOG.info("Query result: {}", result);

        result = advancedQueryService.potOwnedByCip("diego", Collections.singletonList("INSURANCE_AGENT_ROLE_123456"), "B003", 0, 10);
        LOG.info("Query result: {}", result);

        result = advancedQueryService.potOwnedByCip("dario", Collections.singletonList("INSURANCE_AGENT_ROLE_234567"), "B003", 0, 10);
        LOG.info("Query result: {}", result);

        advancedQueryService.endConversation();

    }

}
