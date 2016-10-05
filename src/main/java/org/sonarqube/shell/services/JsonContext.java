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

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.sonarqube.shell.dto.conf.Profile;
import org.sonarqube.shell.dto.out.ChronosEntry;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.util.HashMap;
import java.util.Map;

@Service
public class JsonContext {

    private JAXBContext context;

    public JsonContext() throws JAXBException {
        System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        properties.put(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        properties.put(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
        this.context = JAXBContext.newInstance(new Class[] {ChronosEntry.class, Profile.class}, properties);
    }

    public Marshaller getMarshaller() {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            return marshaller;
        } catch (JAXBException e) {
            throw new IllegalStateException("Bad configuration", e);
        }
    }

    public Unmarshaller getUnmarshaller() {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller;
        } catch (JAXBException e) {
            throw new IllegalStateException("Bad configuration", e);
        }
    }
}
