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

import com.google.common.annotations.VisibleForTesting;
import org.sonarqube.shell.dto.conf.Profile;
import org.sonarqube.shell.dto.in.IssuesPage;
import org.sonarqube.shell.services.ExportToFileService;
import org.sonarqube.shell.services.IssuesPageProcessor;
import org.sonarqube.shell.services.JsonContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.sonarqube.shell.console.Out.consoleOut;
import static org.sonarqube.shell.console.Out.consoleOutAndThrowsException;

@Component
public class ExportCommands implements CommandMarker {

    // query params
    private static final String PROJECT_KEYS = "projectKeys";
    private static final String RULES = "rules";
    private static final String PAGE_INDEX = "p";
    private static final String STATUSES = "statuses";
    private static final String OPEN = "OPEN";

    private static final String EXPORT = "export";
    private static final String RESOURCE = "api/issues/search";
    private static final String NA = "N/A";

    private SonarSession session;
    private ExportToFileService exportToFileService;
    private JsonContext context;

    @Autowired
    public ExportCommands(SonarSession session, ExportToFileService exportToFileService, JsonContext context) {
        this.session = session;
        this.exportToFileService = exportToFileService;
        this.context = context;
    }

    @CliAvailabilityIndicator({EXPORT})
    public boolean isCommandAvailable() {
        return session.isConnected();
    }

    @CliCommand(value = EXPORT, help = "Exports the issues reported by the SonarQube\n"
        + "\tEXAMPLE to export the issues to a file using the maintainability profile:\n\n"
        + "\texport --projectKeys \"closure:library,org.apache.tika:tika,c-family:nginx\" --profile maintainability.json --path issues.json\n")
    public void export(
        @CliOption(key = {PROJECT_KEYS}, help = "Comma-separated list of project keys.", mandatory = true, specifiedDefaultValue = NA, unspecifiedDefaultValue = NA)
        final String projectKeys,
        @CliOption(key = {"profile"}, help = "Path to the profile file.", mandatory = true, specifiedDefaultValue = NA, unspecifiedDefaultValue = NA)
        final String profilePath,
        @CliOption(key = {"path"}, help = "Path to the location where report will be exported.", mandatory = true, specifiedDefaultValue = NA, unspecifiedDefaultValue = NA)
        final String path
    ) {
        Profile profile = getProfile(profilePath);
        Map<String, String> params = buildQueryParams(projectKeys, profile);
        Optional<IssuesPage> page = session.get(RESOURCE, IssuesPage.class, Optional.of(params));

        IssuesPageProcessor processor = new IssuesPageProcessor(profile);
        while (page.isPresent()) {
            processor.processPage(page.get());
            page = getNextPage(page.get(), params);
        }
        exportToFileService.export(processor.getEntries(), new File(path));
    }

    private Map<String, String> buildQueryParams(String projectKeys, Profile profile) {
        Map<String, String> params = new HashMap<>();
        params.put(PROJECT_KEYS, projectKeys);
        params.put(RULES, profile.getAllRules());
        params.put(STATUSES, OPEN);
        return params;
    }

    private Optional<IssuesPage> getNextPage(IssuesPage page, Map<String, String> params) {
        if (page.getPageIndex() * page.getPageSize() >= page.getTotal()) {
            return Optional.empty();
        }

        params.put(PAGE_INDEX, String.valueOf(page.getPageIndex() + 1));
        return session.get(RESOURCE, IssuesPage.class, Optional.of(params));

    }

    @VisibleForTesting
    Profile getProfile(String profile) {
        File path = new File(profile);
        Profile result = new Profile();
        if(!(path.isFile() && path.canRead())) {
            consoleOutAndThrowsException(String.format("Invalid path: '%s'. Please provide a valid path and try again.", path));
        }

        try {

            Unmarshaller unmarshaller = context.getUnmarshaller();
            result = unmarshaller.unmarshal(new StreamSource(path), Profile.class).getValue();
        } catch (JAXBException e) {
            // do nothing because the profile will don't pass the validation
        }

        if(!result.isValid()) {
            consoleOutAndThrowsException(String.format("Failed to parse the profile file: '%s'. Please provide a valid profile and try again.", path));
        }
        consoleOut("Profile has been successfully loaded.\n" + result);
        return result;
    }
}
