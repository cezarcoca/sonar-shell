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
import org.sonarqube.shell.dto.out.ChronosEntry;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExportToFileServiceTest {

    private ExportToFileService sut;

    @Before
    public void setUp() throws JAXBException {
        sut = new ExportToFileService(new JsonContext());
    }

    @Test(expected = NullPointerException.class)
    public void testResultParamShouldBeProvided() {
        sut.export(null, new File(""));
    }

    @Test(expected = NullPointerException.class)
    public void testDestParamShouldBeProvided() {
        sut.export(Collections.emptyList(), null);
    }

    @Test
    public void testShouldExportsTheResultsToTheDestFile() throws IOException {
        // arrange
        Collection<ChronosEntry> result =
            asList(new ChronosEntry("file", "name", "category", 10));
        File dest = File.createTempFile("test", "tmp");
        dest.deleteOnExit();
        // act
        sut.export(result, dest);
        // assert
        assertThat(dest.length() > 0, is(true));
    }
}
