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
package prototypes.ws.proxy.soap.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constantes.ApplicationConfig;
import prototypes.ws.proxy.soap.io.Files;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.validation.SoapValidatorFactory;

public class ProxyConfiguration extends HashMap<String, Object> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ProxyConfiguration.class);

    public static String UID = "proxy.soap.config";

    private final AtomicBoolean validationActive = new AtomicBoolean(true);
    private final AtomicBoolean inBlockingMode = new AtomicBoolean(true);
    private final AtomicInteger nbMaxExchanges = new AtomicInteger(50);
    private String wsdlDirs = "";
    private final AtomicBoolean isPersistedConf = new AtomicBoolean(false);
    private final AtomicBoolean ignoreValidExchanges = new AtomicBoolean(false);
    private final String persistedConfPath = ApplicationConfig.DEFAULT_STORAGE_PATH + "proxy-soap.properties";
    private final AtomicInteger runMode = new AtomicInteger(ApplicationConfig.RUN_MODE_PROD);
    private final AtomicInteger connectTimeout = new AtomicInteger(2000);
    private final AtomicInteger readTimeout = new AtomicInteger(10000);
    private final AtomicInteger persistenceMode = new AtomicInteger(ApplicationConfig.PERSIST_MODE_MEMORY);
    private final AtomicBoolean persistenceXmlMode = new AtomicBoolean(false);
    private String persistenceDbDriver = "org.apache.derby.jdbc.EmbeddedDriver";
    private String persistenceDbUrl = "jdbc:derby:proxy-soap_derby.db";
    private String persistenceDbUsername = "proxy";
    private String persistenceDbPassword = "soap";
    private String persistenceDbProperties = "create=true";

    /**
     * Load default configuration from system properties
     */
    public ProxyConfiguration() {
    }

    public ProxyConfiguration(String validation, String blockingMode,
            String wsdlDirs, String maxRequests, String ignoreValidRequests, String runMode) {
        // check if a persisted configuration is available
        boolean persistedConf = (new File(persistedConfPath)).exists();
        if (persistedConf) {
            LOGGER.info("Persisted configuration File exist");
            loadPersistedConf();
            this.isPersistedConf.set(true);
        } else {
            LOGGER.info("Persisted configuration File doesn't exist");
            setBlockingMode(Boolean.parseBoolean(blockingMode));
            setValidationActive(Boolean.parseBoolean(validation));
            setWsdlDirs(wsdlDirs);
            setNbMaxExchanges(maxRequests);
            setRunMode(runMode);
            setIgnoreValidExchanges(Boolean.parseBoolean(ignoreValidRequests));
        }
    }

    public ProxyConfiguration(boolean validation, boolean blockingMode,
            String wsdlDirs, int maxRequests, boolean ignoreValidRequests, String runMode) {
        setBlockingMode(blockingMode);
        setValidationActive(validation);
        setWsdlDirs(wsdlDirs);
        ProxyConfiguration.this.setNbMaxExchanges(maxRequests);
        setIgnoreValidExchanges(ignoreValidRequests);
    }

    /**
     * return externally configurable properties. Typically those properties
     * will be provided by an IHM or Service to an administrator
     *
     * @return
     */
    public static String[] getKeys() {
        return new String[]{ApplicationConfig.PROP_BLOCKING_MODE,
            ApplicationConfig.PROP_VALIDATION,
            ApplicationConfig.PROP_WSDL_DIRS,
            ApplicationConfig.PROP_MAX_EXCHANGES,
            ApplicationConfig.PROP_IGNORE_VALID_EXCHANGES, //    ApplicationConfig.PROP_PERSIST_MODE,
    };
    }

    /**
     * return internal properties
     *
     * @return
     */
    private static String[] getInternalKeys() {
        return new String[]{ApplicationConfig.PROP_BLOCKING_MODE,
            ApplicationConfig.PROP_VALIDATION,
            ApplicationConfig.PROP_WSDL_DIRS,
            ApplicationConfig.PROP_MAX_EXCHANGES,
            ApplicationConfig.PROP_IGNORE_VALID_EXCHANGES,
            ApplicationConfig.PROP_RUN_MODE,
            ApplicationConfig.PROP_PERSIST_MODE,
            ApplicationConfig.PROP_PERSIST_MODE_DB_XML,
            ApplicationConfig.PROP_PERSIST_MODE_DB_DRIVER,
            ApplicationConfig.PROP_PERSIST_MODE_DB_URL,
            ApplicationConfig.PROP_PERSIST_MODE_DB_USERNAME,
            ApplicationConfig.PROP_PERSIST_MODE_DB_PASSWORD,
            ApplicationConfig.PROP_PERSIST_MODE_DB_PROPERTIES,};
    }

    /**
     *
     * @param obj
     * @return
     */
    public Object get(Object obj) {
        String key = (String) obj;
        if ("blockingMode".equals(key)
                || ApplicationConfig.PROP_BLOCKING_MODE.equals(key)) {
            return isInBlockingMode();
        } else if ("validationActive".equals(key)
                || ApplicationConfig.PROP_VALIDATION.equals(key)) {
            return isValidationActive();
        } else if (ApplicationConfig.PROP_WSDL_DIRS.equals(key)) {
            return getWsdlDirs();
        } else if (ApplicationConfig.PROP_MAX_EXCHANGES.equals(key)) {
            return getNbMaxExchanges();
        } else if (ApplicationConfig.PROP_IGNORE_VALID_EXCHANGES.equals(key)) {
            return isIgnoreValidExchanges();
        } else if (ApplicationConfig.PROP_RUN_MODE.equals(key)) {
            return this.runMode;
        } else if ("persistedConf".equals(key)) {
            return isPersistedConf();
        } else if ("persistedConfPath".equals(key)) {
            return getPersistedConfPath();
        } else if (ApplicationConfig.PROP_PERSIST_MODE.equals(key)) {
            return getPersistenceMode();
        } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_XML.equals(key)) {
            return persistXmlInDb();
        } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_DRIVER.equals(key)) {
            return this.persistenceDbDriver;
        } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_URL.equals(key)) {
            return this.persistenceDbUrl;
        } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_USERNAME.equals(key)) {
            return this.persistenceDbUsername;
        } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_PASSWORD.equals(key)) {
            return this.persistenceDbPassword;
        } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_PROPERTIES.equals(key)) {
            return this.persistenceDbProperties;
        }
        return null;
    }

    public String[] getProperties() {
        return new String[]{"" + isInBlockingMode(),
            "" + isValidationActive(), getWsdlDirs(),
            "" + getNbMaxExchanges()};
    }

    public void setProperty(String key, String value) {
        if (key != null && value != null) {
            if (ApplicationConfig.PROP_BLOCKING_MODE.equals(key)) {
                setBlockingMode(Boolean.parseBoolean(value));
            } else if (ApplicationConfig.PROP_VALIDATION.equals(key)) {
                setValidationActive(Boolean.parseBoolean(value));
            } else if ("wsdls".equals(key)
                    || ApplicationConfig.PROP_WSDL_DIRS.equals(key)) {
                setWsdlDirs(value);
            } else if (ApplicationConfig.PROP_MAX_EXCHANGES.equals(key)) {
                setNbMaxExchanges(value);
            } else if (ApplicationConfig.PROP_IGNORE_VALID_EXCHANGES.equals(key)) {
                setIgnoreValidExchanges(Boolean.parseBoolean(value));
            } else if (ApplicationConfig.PROP_PERSIST_MODE.equals(key)) {
                if (!Strings.isNullOrEmpty(value)) {
                    setPersistenceMode(Integer.parseInt(value));
                }
            } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_XML.equals(key)) {
                if (!Strings.isNullOrEmpty(value)) {
                    setPersistenceXmlMode(Boolean.parseBoolean(value));
                }
            } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_DRIVER.equals(key)) {
                if (!Strings.isNullOrEmpty(value)) {
                    this.persistenceDbDriver = value;
                }
            } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_URL.equals(key)) {
                if (!Strings.isNullOrEmpty(value)) {
                    this.persistenceDbUrl = value;
                }
            } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_USERNAME.equals(key)) {
                if (!Strings.isNullOrEmpty(value)) {
                    this.persistenceDbUsername = value;
                }
            } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_PASSWORD.equals(key)) {
                if (!Strings.isNullOrEmpty(value)) {
                    this.persistenceDbPassword = value;
                }
            } else if (ApplicationConfig.PROP_PERSIST_MODE_DB_PROPERTIES.equals(key)) {
                if (!Strings.isNullOrEmpty(value)) {
                    this.persistenceDbProperties = value;
                }
            }
        }
    }

    public void setRunMode(String env) {
        try {
            this.runMode.set(Integer.parseInt(env));
        } catch (Exception e) {
            this.runMode.set(ApplicationConfig.RUN_MODE_PROD);
        }
    }

    public boolean runInDevMode() {
        return (this.runMode.get() == ApplicationConfig.RUN_MODE_DEV);
    }

    public boolean runInProdMode() {
        return (this.runMode.get() == ApplicationConfig.RUN_MODE_PROD);
    }

    public boolean isValidationActive() {
        return validationActive.get();
    }

    public void setValidationActive(boolean validationActive) {
        this.validationActive.set(validationActive);
    }

    public boolean isInBlockingMode() {
        return inBlockingMode.get();
    }

    public boolean getBlockingMode() {
        return isInBlockingMode();
    }

    public void setBlockingMode(boolean blockingMode) {
        inBlockingMode.set(blockingMode);
    }

    public int getNbMaxExchanges() {
        return nbMaxExchanges.get();
    }

    public void setNbMaxExchanges(int max) {
        nbMaxExchanges.set(max);
    }

    public void setNbMaxExchanges(String max) {
        try {
            nbMaxExchanges.set(Integer.valueOf(max));
        } catch (NumberFormatException e) {
            // previous value will be used
        }
    }

    public String getWsdlDirs() {
        return wsdlDirs;
    }

    public String getWsdls() {
        return wsdlDirs;
    }

    public void setWsdlDirs(String dirs) {
        if ((dirs != null) && !dirs.equals(this.wsdlDirs)) {
            this.wsdlDirs = dirs;
            reloadWsdl();
        }
    }

    public void reloadWsdl() {
        // if validation is currently active
        // we need to unactivate it during the reload time
        boolean wasValidationActive = this.isValidationActive();
        if (wasValidationActive) {
            setValidationActive(false);
        }
        try {
            SoapValidatorFactory.getInstance().createSoapValidators(this.wsdlDirs);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        // reactivation even if exceptions happened
        if (wasValidationActive) {
            setValidationActive(true);
        }
    }

    public boolean isIgnoreValidExchanges() {
        return this.ignoreValidExchanges.get();
    }

    public void setIgnoreValidExchanges(boolean ignore) {
        this.ignoreValidExchanges.set(ignore);
    }

    public int getConnectTimeout() {
        return connectTimeout.get();
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout.set(connectTimeout);
    }

    public int getReadTimeout() {
        return readTimeout.get();
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout.set(readTimeout);
    }

    public Integer getPersistenceMode() {
        return persistenceMode.get();
    }

    public void setPersistenceMode(Integer persistenceMode) {
        this.persistenceMode.set(persistenceMode);
    }

    public boolean persistXmlInDb() {
        return persistenceXmlMode.get();
    }

    public void setPersistenceXmlMode(Boolean persistenceXmlMode) {
        this.persistenceXmlMode.set(persistenceXmlMode);
    }

    public String getPersistenceDbDriver() {
        return persistenceDbDriver;
    }

    public String getPersistenceDbUrl() {
        return persistenceDbUrl;
    }

    public String getPersistenceDbUsername() {
        return persistenceDbUsername;
    }

    public String getPersistenceDbPassword() {
        return persistenceDbPassword;
    }

    public String getPersistenceDbProperties() {
        return persistenceDbProperties;
    }

    public boolean isPersistedConf() {
        return this.isPersistedConf.get();
    }

    public String getPersistedConfPath() {
        return this.persistedConfPath;
    }

    public String persistConf() {
        LOGGER.debug("Persist configuration to " + this.persistedConfPath);
        Files.write(this.persistedConfPath, toProperties());
        return persistedConfPath;
    }

    private void loadPersistedConf() {
        LOGGER.debug("Load persisted configuration from " + this.persistedConfPath);
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File(persistedConfPath)));
            for (String key : getInternalKeys()) {
                if (props.getProperty(key) != null) {
                    this.setProperty(key, props.getProperty(key));
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public String toProperties() {
        StringBuilder sb = new StringBuilder();
        for (String key : getInternalKeys()) {
            if (this.get(key) != null) {
                sb.append(key);
                sb.append("=");
                sb.append(this.get(key));
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ProxyConfiguration{" + "validationActive=" + validationActive + ", inBlockingMode=" + inBlockingMode + ", nbMaxRequests=" + nbMaxExchanges + ", wsdlDirs=" + wsdlDirs + ", isPersisted=" + isPersistedConf + ", ignoreValidRequests=" + ignoreValidExchanges + ", persistPath=" + persistedConfPath + ", runMode=" + runMode + '}';
    }

}
