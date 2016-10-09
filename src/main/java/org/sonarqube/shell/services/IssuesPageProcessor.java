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

package org.sonarqube.shell.services;

import org.sonarqube.shell.dto.conf.Profile;
import org.sonarqube.shell.dto.in.Component;
import org.sonarqube.shell.dto.in.Issue;
import org.sonarqube.shell.dto.in.IssuesPage;
import org.sonarqube.shell.dto.out.ChronosEntry;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static org.sonarqube.shell.console.Out.consoleOut;

public class IssuesPageProcessor {

    private final Collection<ChronosEntry> entries;
    private final Profile profile;
    private Map<String, String> rulesMapping;

    public IssuesPageProcessor(Profile profile) {
        this.entries = new ArrayList<>();
        this.profile = profile;
        initRulesToAxeNameMapping(profile);
    }

    private void initRulesToAxeNameMapping(Profile profile) {
        rulesMapping = new HashMap<>();
        profile.getAxes().stream().forEach(a -> a.getRules().forEach(r -> rulesMapping.put(r, a.getName())));
    }

    public void processPage(IssuesPage page) {
        printInfo(page);
        Map<Integer, List<Issue>> issues =
            page.getIssues().stream().collect(groupingBy(Issue::getComponentId));
        page.getComponents().stream()
            .filter(c -> c.getQualifier().equals("FIL"))
            .forEach(c -> entries.add(getChronosEntry(issues.get(c.getId()), c)));
    }

    public Collection<ChronosEntry> getEntries() {
        return Collections.unmodifiableCollection(entries);
    }

    private ChronosEntry getChronosEntry(List<Issue> issues, Component component) {
        int total = issues.stream().mapToInt(Issue::getEffort).sum();
        String rule = issues.get(0).getRule();
        return new ChronosEntry(component.getPath(), rulesMapping.get(rule), profile.getCategory(), total);
    }

    private void printInfo(IssuesPage page) {
        int start = 1 + (page.getPageIndex() - 1) * page.getPageSize();
        int end = page.getPageIndex() * page.getPageSize();
        if (end > page.getTotal()) {
            end = page.getTotal();
        }
        consoleOut(String.format("Processing range %d..%d from a total of %d", start, end, page.getTotal()));
    }
}
