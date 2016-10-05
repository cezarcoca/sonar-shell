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
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ListProjectsCommands implements CommandMarker {

    private static final String LIST_PROJECTS = "list projects";
    private SonarSession session;

    @Autowired
    public ListProjectsCommands(SonarSession session) {
        this.session = session;
    }

    @CliAvailabilityIndicator({LIST_PROJECTS})
    public boolean isCommandAvailable() {
        return session.isConnected();
    }

    @CliCommand(value = LIST_PROJECTS, help = "Lists the projects published on the SonarQube")
    public void disconnect() {
        session.disconnect(Optional.empty());
    }
}
