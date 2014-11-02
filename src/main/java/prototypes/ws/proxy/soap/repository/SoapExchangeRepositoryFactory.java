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
package prototypes.ws.proxy.soap.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.constantes.ApplicationConfig;
import prototypes.ws.proxy.soap.repository.jpa.SoapExchangeRepositoryJpa;
import prototypes.ws.proxy.soap.repository.memory.SoapExchangeRepositoryInMemory;

/**
 *
 * @author jlamande
 */
public class SoapExchangeRepositoryFactory {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SoapExchangeRepositoryFactory.class);

    /**
     * private Constructor
     */
    private SoapExchangeRepositoryFactory() {
    }

    /**
     * Holder
     */
    private static class SingletonHolder {

        /**
         * Unique instance
         */
        private final static SoapExchangeRepositoryFactory instance = new SoapExchangeRepositoryFactory();
    }

    /**
     * Access to the unique instance
     */
    public static SoapExchangeRepositoryFactory getInstance() {
        return SingletonHolder.instance;
    }

    public SoapExchangeRepository createRepository(ProxyConfiguration proxyConfig) {
        if (ApplicationConfig.PERSIST_MODE_DB.equals(proxyConfig.getPersistenceMode())) {
            try {
                return new SoapExchangeRepositoryJpa(proxyConfig);
            } catch (RuntimeException ex) {
                // fall back to a in-memory implementation
                return new SoapExchangeRepositoryInMemory(proxyConfig);
            }
        } else {
            return new SoapExchangeRepositoryInMemory(proxyConfig);
        }
    }

}