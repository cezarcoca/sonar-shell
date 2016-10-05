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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.*;

import static java.util.Objects.requireNonNull;

@Service
public class ExportToFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportToFileService.class);
    private JsonContext context;

    @Autowired
    public ExportToFileService(JsonContext context) {
        this.context = context;
    }

    public void export(Object result, File dest) {
        requireNonNull(result);
        requireNonNull(dest);
        try (
            FileOutputStream fos = new FileOutputStream(dest);
            OutputStream out = new BufferedOutputStream(fos)) {
            context.getMarshaller().marshal(result, out);
        } catch (JAXBException e) {
            LOGGER.error("Failed to marshall the result.", e);
        } catch (IOException e) {
            LOGGER.error("Failed to save the file.", e);
        }
    }
}
