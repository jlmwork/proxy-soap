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
import prototypes.ws.proxy.soap.validation.SoapValidatorFactory;

public class ProxyConfiguration extends HashMap<String, Object> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ProxyConfiguration.class);

    public static String KEY = "proxy.soap.config";

    private final AtomicBoolean validationActive = new AtomicBoolean(true);
    private final AtomicBoolean inBlockingMode = new AtomicBoolean(true);
    private final AtomicInteger nbMaxRequests = new AtomicInteger(50);
    private String wsdlDirs = "";
    private final AtomicBoolean isPersisted = new AtomicBoolean(false);
    private String persistPath = System.getProperty("java.io.tmpdir") + File.separator + "proxy-soap.properties";

    /**
     * Load default configuration from system properties
     */
    public ProxyConfiguration() {
    }

    public ProxyConfiguration(String validation, String blockingMode,
            String wsdlDirs, String maxRequests) {
        // check if a persisted configuration is available
        boolean persistedConf = (new File(persistPath)).exists();
        if (persistedConf) {
            LOGGER.info("Persisted configuration File exist");
            loadPersistedConf();
            this.isPersisted.set(true);
        } else {
            LOGGER.info("Persisted configuration File doesn't exist");
            setBlockingMode(Boolean.parseBoolean(blockingMode));
            setValidationActive(Boolean.parseBoolean(validation));
            setWsdlDirs(wsdlDirs);
            setNbMaxRequests(maxRequests);
        }
    }

    public ProxyConfiguration(boolean validation, boolean blockingMode,
            String wsdlDirs, int maxRequests) {
        setBlockingMode(blockingMode);
        setValidationActive(validation);
        setWsdlDirs(wsdlDirs);
        setNbMaxRequests(maxRequests);
    }

    public static String[] getKeys() {
        return new String[]{ApplicationConfig.PROP_BLOCKING_MODE,
            ApplicationConfig.PROP_VALIDATION,
            ApplicationConfig.PROP_WSDL_DIRS,
            ApplicationConfig.PROP_MAX_REQUESTS};
    }

    public Object get(Object obj) {
        String key = (String) obj;
        if ("blockingMode".equals(key)
                || ApplicationConfig.PROP_BLOCKING_MODE.equals(key)) {
            return isInBlockingMode();
        } else if ("validationActive".equals(key)
                || ApplicationConfig.PROP_VALIDATION.equals(key)) {
            return isValidationActive();
        } else if ("wsdls".equals(key)
                || ApplicationConfig.PROP_WSDL_DIRS.equals(key)) {
            return getWsdlDirs();
        } else if ("nbMaxRequests".equals(key)
                || ApplicationConfig.PROP_MAX_REQUESTS.equals(key)) {
            return getNbMaxRequests();
        } else if ("persisted".equals(key)) {
            return isPersisted();
        } else if ("persistPath".equals(key)) {
            return getPersistPath();
        }
        return null;
    }

    public String[] getProperties() {
        return new String[]{"" + isInBlockingMode(),
            "" + isValidationActive(), getWsdlDirs(),
            "" + getNbMaxRequests()};
    }

    public void setProperty(String key, String value) {
        if (key != null && value != null) {
            if ("blockingMode".equals(key)
                    || ApplicationConfig.PROP_BLOCKING_MODE.equals(key)) {
                setBlockingMode(Boolean.parseBoolean(value));
            } else if ("validationActive".equals(key)
                    || ApplicationConfig.PROP_VALIDATION.equals(key)) {
                setValidationActive(Boolean.parseBoolean(value));
            } else if ("wsdls".equals(key)
                    || ApplicationConfig.PROP_WSDL_DIRS.equals(key)) {
                setWsdlDirs(value);
            } else if ("nbMaxRequests".equals(key)
                    || ApplicationConfig.PROP_MAX_REQUESTS.equals(key)) {
                setNbMaxRequests(value);
            }
        }
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

    public int getNbMaxRequests() {
        return nbMaxRequests.get();
    }

    public void setNbMaxRequests(int max) {
        nbMaxRequests.set(max);
    }

    public void setNbMaxRequests(String max) {
        try {
            nbMaxRequests.set(Integer.valueOf(max));
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
            SoapValidatorFactory.createSoapValidators(this.wsdlDirs);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        // reactivation even if exceptions happened
        if (wasValidationActive) {
            setValidationActive(true);
        }
    }

    public boolean isPersisted() {
        return this.isPersisted.get();
    }

    public String getPersistPath() {
        return this.persistPath;
    }

    public String persist() {
        LOGGER.debug("Persist configuration to " + this.persistPath);
        Files.write(this.persistPath, toProperties());
        return persistPath;
    }

    private void loadPersistedConf() {
        LOGGER.debug("Load persisted configuration from " + this.persistPath);
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File(persistPath)));
            for (String key : getKeys()) {
                this.setProperty(key, props.getProperty(key));
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public String toProperties() {
        StringBuilder sb = new StringBuilder();
        for (String key : getKeys()) {
            sb.append(key);
            sb.append("=");
            sb.append(this.get(key));
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ProxyConfiguration [validationActive=" + validationActive
                + ", inBlockingMode=" + inBlockingMode + ", nbMaxRequests="
                + nbMaxRequests + ", wsdlDirs=" + wsdlDirs + "]";
    }

}
