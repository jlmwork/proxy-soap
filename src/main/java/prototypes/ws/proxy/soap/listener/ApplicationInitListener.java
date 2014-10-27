/*
 * Copyright 2014 jlamande.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package prototypes.ws.proxy.soap.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.constantes.ApplicationConfig;
import prototypes.ws.proxy.soap.context.ApplicationContext;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;
import prototypes.ws.proxy.soap.repository.jpa.SoapExchangeRepositoryJpa;

/**
 *
 * @author julamand
 */
public class ApplicationInitListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ApplicationInitListener.class);

    public ApplicationInitListener() {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Application starting");

        // load configuration
        ProxyConfiguration proxyConfig = new ProxyConfiguration(
                get(ApplicationConfig.PROP_VALIDATION, "true"),
                get(ApplicationConfig.PROP_BLOCKING_MODE, "false"),
                get(ApplicationConfig.PROP_WSDL_DIRS, ""),
                get(ApplicationConfig.PROP_MAX_REQUESTS, "50"),
                get(ApplicationConfig.PROP_IGNORE_VALID_REQUESTS, "false"),
                get(ApplicationConfig.PROP_RUN_MODE, ""));
        LOGGER.debug(proxyConfig.toString());

        // save config
        ApplicationContext.setProxyConfiguration(sce.getServletContext(), proxyConfig);

        //SoapExchangeRepository exchangesRepo = new SoapExchangeRepositoryInMemory(proxyConfig);
        SoapExchangeRepository exchangesRepo = new SoapExchangeRepositoryJpa(proxyConfig);
        ApplicationContext.setSoapExchangeRepository(sce.getServletContext(), exchangesRepo);

    }

    private String get(String prop, String def) {
        return System.getProperty(prop, def);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Application stopped");
        SoapExchangeRepository exchangesRepo = ApplicationContext.getSoapExchangeRepository(sce.getServletContext());
        if (exchangesRepo != null) {
            exchangesRepo.close();
        }
    }
}
