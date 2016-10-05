/*
 *
 *  * Copyright 2016 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.sonarqube.shell.commands;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonarqube.shell.dto.in.Status;
import org.springframework.stereotype.Component;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.sonarqube.shell.console.Out.consoleOut;

@Component
public class SonarSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonarSession.class);

    private WebTarget rootContext;
    private Client client;

    public SonarSession() {
        ClientConfig config = new ClientConfig();
        config.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.HEADERS_ONLY);
        client = ClientBuilder.newClient(config);
    }

    void connect(String host, Integer port, String protocol) {
        try {
            URI uri = new URL(protocol, host, port, "").toURI();
            consoleOut(String.format("Connecting to: %s", uri));
            rootContext = client.target(uri);
            Optional<Status> status = get("api/system/status", Status.class, Optional.empty());
            if (status.isPresent()) {
                consoleOut("Successfully connected to: " + rootContext.getUri());
                consoleOut("Server " + status.get().toString());
                return;
            }
        } catch (URISyntaxException | MalformedURLException e) {
            LOGGER.error("Failed to create URI", e);
        }
        disconnect(Optional.of("Server connection failed"));
    }

    <T> Optional<T> get(String path, Class<T> clazz, Optional<Map<String, String>> params) {
        try {
            WebTarget resource = rootContext.path(path);
            if (params.isPresent()) {
                for (Map.Entry<String, String> entry : params.get().entrySet()) {
                    resource = resource.queryParam(entry.getKey(), entry.getValue());
                }
            }
            return Optional.of(resource.request(MediaType.APPLICATION_JSON_TYPE).get(clazz));
        } catch (ProcessingException e) {
            LOGGER.error("Failed to get the resource {} and convert it to {}", path, clazz.getName());
        }
        return Optional.empty();
    }

    void disconnect(Optional<String> message) {
        consoleOut(message.orElse(
            "Successfully disconnected from: " + rootContext.getUri()));
        rootContext = null;
    }

    public boolean isConnected() {
        return nonNull(rootContext);
    }

    public String getActiveSession() {
        return rootContext == null ? "disconnected" : rootContext.getUri().getHost();
    }
}
