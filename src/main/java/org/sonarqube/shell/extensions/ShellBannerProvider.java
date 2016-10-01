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

package org.sonarqube.shell.extensions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.shell.support.util.FileUtils;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShellBannerProvider implements BannerProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BannerProvider.class);

    @Override
    public String getBanner() {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.readBanner(ShellBannerProvider.class, "/banner.txt"));
        sb.append(getVersion()).append(OsUtils.LINE_SEPARATOR);
        sb.append(OsUtils.LINE_SEPARATOR);

        return sb.toString();
    }


    @Override
    public String getVersion() {
        try (InputStream in = ShellBannerProvider.class.getResourceAsStream("/version.properties")) {

            Properties properties = new Properties();
            properties.load(in);
            return properties.getProperty("shell.version");
        } catch (IOException e) {
            LOGGER.error("Failed to read version file", e);
        }
        return "unknown";
    }

    @Override
    public String getWelcomeMessage() {
        return "Welcome to " + getProviderName()
            + ". For assistance press or type \"help\" then hit ENTER.";
    }

    @Override
    public String getProviderName() {
        return "SonarQube Shell";
    }
}
