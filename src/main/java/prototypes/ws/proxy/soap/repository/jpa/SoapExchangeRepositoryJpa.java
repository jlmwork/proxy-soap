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

import java.io.File;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.eclipse.persistence.internal.jpa.metamodel.ManagedTypeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.constantes.ApplicationConfig;
import prototypes.ws.proxy.soap.io.Files;
import prototypes.ws.proxy.soap.io.SoapExchange;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;
import prototypes.ws.proxy.soap.time.Dates;

/**
 *
 * @author jlamande
 */
public class SoapExchangeRepositoryJpa extends SoapExchangeRepository {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SoapExchangeRepositoryJpa.class);

    private final EntityManagerFactory emf;

    private final Boolean xmlInDbs;

    private final String persistenceUnitName;

    public SoapExchangeRepositoryJpa(ProxyConfiguration proxyConfig) {
        super(proxyConfig);
        // for use of a derby db
        String derbyHome = ApplicationConfig.DEFAULT_STORAGE_PATH;
        LOGGER.info("DERBY HOME : " + derbyHome);
        System.setProperty("derby.system.home", derbyHome);
        System.setProperty("derby.database.forceDatabaseLock", "false");
        cleanupDb();
        /*
         try {
         InitialContext ic = new InitialContext();
         // Construct BasicDataSource
         BasicDataSource bds = new BasicDataSource();
         bds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
         bds.setUrl("jdbc:derby:proxy-soap_derby.db;create=true");
         bds.setUsername("proxy");
         bds.setPassword("soap");
         ic.bind("jdbc/proxyDS", bds);
         } catch (NamingException nE) {
         LOGGER.warn("Datasource creation failed : " + nE.getMessage(), nE);
         }*/
        // externalize DB connection
        Properties connectionProps = new Properties();
        connectionProps.setProperty("javax.persistence.jdbc.driver", proxyConfig.getPersistenceDbDriver());
        connectionProps.setProperty("javax.persistence.jdbc.url", proxyConfig.getPersistenceDbUrl() + ";" + proxyConfig.getPersistenceDbProperties());
        connectionProps.setProperty("javax.persistence.jdbc.user", proxyConfig.getPersistenceDbUsername());
        connectionProps.setProperty("javax.persistence.jdbc.password", proxyConfig.getPersistenceDbPassword());
        xmlInDbs = proxyConfig.persistXmlInDb();
        persistenceUnitName = (proxyConfig.persistXmlInDb()) ? "ProxyPUFull" : "ProxyPU";
        emf = (EntityManagerFactory) Persistence.createEntityManagerFactory(persistenceUnitName, connectionProps);
        //try to start
        emf.createEntityManager().close();
    }

    @Override
    public SoapExchange get(String id) {
        EntityManager em = emf.createEntityManager();
        SoapExchange exchange = em.createQuery("select s from SoapExchange s where id=:id", SoapExchange.class).setParameter("id", id).getSingleResult();
        em.detach(exchange);
        if (!xmlInDbs) {
            exchange.setRequest(Files.read(getRequestFilePath(exchange)));
            exchange.setResponse(Files.read(getRequestFilePath(exchange)));
        }
        return exchange;
    }

    @Override
    public SoapExchange get(String id, String[] fields) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<SoapExchange> list() {
        LOGGER.debug("get soap exchanges from db");
        List<SoapExchange> exchanges = listWithoutContent();
        if (!xmlInDbs) {
            // TODO : eager loading - not a good idea for high volumes
            for (SoapExchange exchange : exchanges) {
                // em.detach(exchange);
                exchange.setRequest(Files.read(getRequestFilePath(exchange)));
                exchange.setResponse(Files.read(getRequestFilePath(exchange)));
            }
        }
        LOGGER.debug("get soap exchanges from db {} ", exchanges.size());
        return exchanges;
    }

    @Override
    public List<SoapExchange> listWithoutContent() {
        LOGGER.debug("get soap exchanges from db");
        EntityManager em = emf.createEntityManager();
        List<SoapExchange> exchanges = em.createQuery("select s from SoapExchange s ORDER BY s.time DESC", SoapExchange.class).getResultList();
        LOGGER.debug("get soap exchanges from db {} ", exchanges.size());
        return exchanges;
    }

    @Override
    public void save(SoapExchange exchange) {
        EntityManager em = null;
        if (!ignoreExchange(exchange)) {
            em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(exchange);
                em.getTransaction().commit();
                if (!xmlInDbs) {
                    Files.write(getRequestFilePath(exchange), exchange.getRequestAsXML());
                    Files.write(getResponseFilePath(exchange), exchange.getResponseAsXML());
                }
                LOGGER.debug("exchange saved");
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                LOGGER.debug("Persistence Exception Stack : ", e);
            } finally {
                closeTransaction(em);
            }
        }
    }

    private String getRequestFilePath(SoapExchange exchange) {
        String dirPath = ApplicationConfig.EXCHANGES_STORAGE_PATH + Dates.getFormattedDate(exchange.getTime(), Dates.YYYYMMDD_HH) + File.separator;
        LOGGER.debug("Requests files path : " + dirPath);
        (new File(dirPath)).mkdirs();
        return dirPath + exchange.getId() + "-request.xml";
    }

    private String getResponseFilePath(SoapExchange exchange) {
        String dirPath = ApplicationConfig.EXCHANGES_STORAGE_PATH + Dates.getFormattedDate(exchange.getTime(), Dates.YYYYMMDD_HH) + File.separator;
        LOGGER.debug("Response files path : " + dirPath);
        (new File(dirPath)).mkdirs();
        return dirPath + exchange.getId() + "-response.xml";
    }

    @Override
    public synchronized void removeAll() {
        LOGGER.info("Remove all SoapExchanges");
        EntityManager em = null;
        try {
            // try truncate
            em = emf.createEntityManager();
            // will use a more complex method to use truncate rather than delete
            // so we need the table name of the entity
            String table = ((ManagedTypeImpl) emf.getMetamodel().managedType(SoapExchange.class)).getDescriptor().getTableName();
            em.getTransaction().begin();
            try {
                em.createNativeQuery("truncate table " + table).executeUpdate();
                if (!xmlInDbs) {
                    Files.deleteDirectory(ApplicationConfig.EXCHANGES_STORAGE_PATH);
                }
            } catch (Exception truncE) {
                LOGGER.warn("Error on TRUNCATE operation " + truncE.getMessage());
                em.createQuery("delete from SoapExchange").executeUpdate();
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            closeTransaction(em);
        }
    }

    @Override
    public void close() {
        try {
            emf.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        try {
            // try shutdown DB, useful for some DB platforms (derby)
            Properties connectionProps = new Properties();
            connectionProps.setProperty("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
            connectionProps.setProperty("javax.persistence.jdbc.url", "jdbc:derby:;shutdown=true");
            EntityManagerFactory emfClose = Persistence.createEntityManagerFactory(persistenceUnitName, connectionProps);
            emfClose.close();
            cleanupDb();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    private void closeTransaction(EntityManager em) {
        if (em != null) {
            // Close the database connection:
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    private void cleanupDb() {
        Files.deleteFile(ApplicationConfig.DEFAULT_STORAGE_PATH + "proxy-soap_derby.db" + File.separator + "db.lck");
        Files.deleteFile(ApplicationConfig.DEFAULT_STORAGE_PATH + "proxy-soap_derby.db" + File.separator + "dbex.lck");
    }

}
