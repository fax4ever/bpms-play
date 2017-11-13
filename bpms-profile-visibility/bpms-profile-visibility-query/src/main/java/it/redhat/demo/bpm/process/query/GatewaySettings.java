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

/**
 * @author Fabio Massimo Ercoli (C) 2017 Red Hat Inc.
 */
public class GatewaySettings {

    // mandatory fields
    private String username;
    private String password;

    // optional fields
    private String hostname;
    private Integer port;
    private String protocol;
    private Integer timeout;
    private String contextPath;

    private GatewaySettings() {
    }

    public static GatewaySettings create(String username, String password) {

        GatewaySettings instance = new GatewaySettings();
        instance.username = username;
        instance.password = password;

        // set default values
        //TODO change values!!!
        instance.hostname = "localhost";
        instance.port = 8080;
        instance.protocol = "http";
        instance.timeout = 30000;
        instance.contextPath = "kie-server";

        return instance;

    }

    public GatewaySettings hostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public GatewaySettings port(Integer port) {
        this.port = port;
        return this;
    }

    public GatewaySettings protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public GatewaySettings hostname(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    public GatewaySettings contextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }

    public Integer getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public String getContextPath() {
        return contextPath;
    }

}
