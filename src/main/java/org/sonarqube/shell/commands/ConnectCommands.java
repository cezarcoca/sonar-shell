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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

import static java.util.Arrays.asList;

@Component
public class ConnectCommands implements CommandMarker {

    private static final String CONNECT = "connect";
    private static final Collection<String> SUPPORTED_PROTOCOLS = asList("http", "https");
    private SonarSession session;

    @Autowired
    public ConnectCommands(SonarSession session) {
        this.session = session;
    }

    @CliCommand(value = CONNECT, help = "Connects to the SonarQube server."
        + "If this is not specified, attempts to connect to port 9000.\n"
        + "\tEXAMPLE to connect to SonarQube:\n\n"
        + "\tconnect --host sonarqube.com --port 443 --protocol https\n"
    )
    public void connect(
        @CliOption(key = {"host"}, help = "Specifies the name of the host machine where the SonarQube is running. "
            + "If this is not specified, attempts to connect to a SonarQube process running on the localhost.",
            unspecifiedDefaultValue = "localhost",
            specifiedDefaultValue = "localhost"
        )
        final String host,
        @CliOption(key = {"port"}, help = "Specifies the port where the SonarQube is listening.",
            unspecifiedDefaultValue = "9000",
            specifiedDefaultValue = "9000")
        final Integer port,
        @CliOption(key = "protocol", help = "Specified the protocol used to connect to SonarQube."
            + "If this is not specified, the HTTP protocol is used.",
            unspecifiedDefaultValue = "http",
            specifiedDefaultValue = "http")
        final String protocol) {
        String sonarHost = Objects.isNull(host) ? "localhost" : host;
        Integer sonarPort = Objects.isNull(port) ? 9000 : port;
        String sonarProtocol = Objects.isNull(protocol) || !SUPPORTED_PROTOCOLS.contains(protocol.toLowerCase()) ? "http" : protocol;
        session.connect(sonarHost, sonarPort, sonarProtocol);
    }

    @CliAvailabilityIndicator({CONNECT})
    public boolean isCommandAvailable() {
        return !session.isConnected();
    }
}
