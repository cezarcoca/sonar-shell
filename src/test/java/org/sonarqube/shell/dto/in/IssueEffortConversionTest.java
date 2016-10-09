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

package org.sonarqube.shell.dto.in;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.sonarqube.shell.dto.in.Issue;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class IssueEffortConversionTest {

    @Parameterized.Parameter
    public String input;
    @Parameterized.Parameter(value = 1)
    public Integer expected;
    private Issue sut;

    @Parameterized.Parameters(name = "Given value is {0} then the generated integer is {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{"123min", 123}, {"invalid", 0}, {"100", 100}});
    }

    @Before
    public void setUp() {
        sut = new Issue("rule", "severity", 1, input);
    }

    @Test
    public void test() {
        assertThat(sut.getEffort(), is(expected));
    }
}
