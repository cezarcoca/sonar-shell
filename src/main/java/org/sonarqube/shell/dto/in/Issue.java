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

import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;

@Getter
@ToString
public class Issue {

    @XmlElement(name = "rule")
    private String rule;
    @XmlElement(name = "severity")
    private String severity;
    @XmlElement(name = "componentId")
    private Integer componentId;
    @XmlElement(name = "effort")
    private String effort;
}
