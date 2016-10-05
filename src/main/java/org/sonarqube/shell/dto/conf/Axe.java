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

package org.sonarqube.shell.dto.conf;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.stream.Collectors.joining;

@Getter
@ToString
@EqualsAndHashCode
@XmlAccessorType(XmlAccessType.FIELD)
public class Axe {
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "rules")
    private Collection<String> rules;

    public Axe() {
        this("", new ArrayList<>());
    }

    public Axe(String name, Collection<String> rules) {
        this.name = name;
        this.rules = rules;
    }

    public String getRulesAsString() {
        return rules.stream().collect(joining(",", "", ""));
    }

    public boolean isValid() {
        return !(name.isEmpty() || rules.isEmpty());
    }
}
