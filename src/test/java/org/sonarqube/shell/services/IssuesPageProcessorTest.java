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

import org.junit.Before;
import org.junit.Test;
import org.sonarqube.shell.dto.conf.Axis;
import org.sonarqube.shell.dto.conf.Profile;
import org.sonarqube.shell.dto.in.Component;
import org.sonarqube.shell.dto.in.Issue;
import org.sonarqube.shell.dto.in.IssuesPage;
import org.sonarqube.shell.dto.out.ChronosEntry;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IssuesPageProcessorTest {

    public static final String CRITICAL = "critical";
    public static final String HIGH_CYCLO = "highCyclo";
    public static final String MANY_PARAMETERS = "manyParameters";
    private static final Integer COMPONENT_ID = 1;

    private Profile profile;
    private IssuesPageProcessor sut;

    @Before
    public void setUp() {

        List<Axis> axes =
            asList(new Axis(HIGH_CYCLO, asList("javascript:cc", "squid:cc")), new Axis(MANY_PARAMETERS, asList("squid:107", "cpp:107")));
        profile = new Profile("SonarQube", axes);
        sut = new IssuesPageProcessor(profile);
    }

    @Test
    public void testShouldSumIssuesBelongingToTheSameAxisAndComponent() {
        // arrange
        Component component = new Component(COMPONENT_ID, "FIL", "/src/main/java/Main.java", 1, 1);
        IssuesPage page =
            new IssuesPage(4, 1, 100, asList(new Issue("javascript:cc", CRITICAL, COMPONENT_ID, "10min"), new Issue("squid:cc", CRITICAL, COMPONENT_ID, "15min"), new Issue("squid:cc", CRITICAL, COMPONENT_ID, "2min"), new Issue("squid:107", CRITICAL, COMPONENT_ID, "19min"), new Issue("cpp:107", CRITICAL, COMPONENT_ID, "21min")), asList(component));
        // act
        sut.processPage(page);
        // assert
        assertThat(sut.getEntries().size(), is(2));
        sut.getEntries().forEach(e -> {
            switch (e.getName()) {
                case HIGH_CYCLO:
                    assertThat(e, is(new ChronosEntry(component.getPath(), HIGH_CYCLO, profile.getCategory(), 27)));
                    break;
                case MANY_PARAMETERS:
                    assertThat(e, is(new ChronosEntry(component.getPath(), MANY_PARAMETERS, profile.getCategory(), 40)));
                    break;
                default:
                    throw new AssertionError("Unexpected axis name.");
            }
        });
    }
}
