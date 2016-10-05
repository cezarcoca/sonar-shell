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

import org.junit.Before;
import org.junit.Test;
import org.sonarqube.shell.dto.conf.Axe;
import org.sonarqube.shell.dto.conf.Profile;
import org.sonarqube.shell.services.JsonContext;

import javax.xml.bind.JAXBException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExportCommandsTest {

    private ExportCommands sut;

    @Before
    public void setUp() throws JAXBException {
        // TODO - add mockito to mocks the all dependencies but JsonContext
        sut = new ExportCommands(null, null, new JsonContext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCanHandleInvalidPaths() {
        sut.getProfile("invalid path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCanHanleMalformedFormat() {
        sut.getProfile(resolvePath("/invalid_profile.json"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCanHandleEmptyProfile() {
        sut.getProfile(resolvePath("/empty.json"));
    }

    @Test
    public void testValidProfilesAreSuccessfullyUnmarshalled() {
        // arrange
        List<Axe> axes = asList(new Axe("complexity", asList("javascript:FunctionComplexity", "squid:MethodCyclomaticComplexity")), new Axe("readability", asList("javascript:NestedIfDepth")));
        Profile expected = new Profile("maintainability", axes);
        System.out.println(expected.getAllRules());
        // act
        Profile actual = sut.getProfile(resolvePath("/valid_profile.json"));
        // assert
        assertThat(actual, is(expected));
    }

    private String resolvePath(String file) {
        return ExportCommandsTest.class.getResource(file).getPath();
    }
}
