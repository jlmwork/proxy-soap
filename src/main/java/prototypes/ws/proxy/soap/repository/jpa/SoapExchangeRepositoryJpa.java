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
package prototypes.ws.proxy.soap.repository.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.constantes.ApplicationConfig;
import prototypes.ws.proxy.soap.io.SoapExchange;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;

/**
 *
 * @author jlamande
 */
public class SoapExchangeRepositoryJpa extends SoapExchangeRepository {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SoapExchangeRepositoryJpa.class);

    private EntityManagerFactory emf;

    public SoapExchangeRepositoryJpa(ProxyConfiguration proxyConfig) {
        super(proxyConfig);
        // for use of a derby db
        String derbyHome = ApplicationConfig.DEFAULT_STORAGE_PATH;
        LOGGER.info("DERBY HOME : " + derbyHome);
        System.setProperty("derby.system.home", derbyHome);
        emf = (EntityManagerFactory) Persistence.createEntityManagerFactory("ProxyPU");

    }

    @Override
    public SoapExchange get(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<SoapExchange> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save(SoapExchange exchange) {
        if (!ignoreExchange(exchange)) {
            EntityManager em = null;//Requests.getEntityManagerFactory(this.getServletContext()).createEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(exchange);
                em.getTransaction().commit();
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            } finally {
                if (em != null) {
                    // Close the database connection:
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    em.close();
                }
            }
        }
    }

    @Override
    public synchronized void removeAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        try {
            emf.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
