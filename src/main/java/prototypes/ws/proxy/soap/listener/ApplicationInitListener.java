package prototypes.ws.proxy.soap.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.constantes.ApplicationConfig;
import prototypes.ws.proxy.soap.io.Requests;
import prototypes.ws.proxy.soap.validation.SoapValidatorFactory;

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
        ProxyConfiguration proxy = new ProxyConfiguration(
                get(ApplicationConfig.PROP_VALIDATION, "true"),
                get(ApplicationConfig.PROP_BLOCKING_MODE, "false"),
                get(ApplicationConfig.PROP_WSDL_DIRS, ""),
                get(ApplicationConfig.PROP_MAX_REQUESTS, "50"));
        LOGGER.debug(proxy.toString());
        sce.getServletContext().setAttribute(ProxyConfiguration.KEY, proxy);
        Requests.getMonitorManager(sce.getServletContext());
        SoapValidatorFactory.listValidators();
    }

    private String get(String prop, String def) {
        return System.getProperty(prop, def);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Application stopped");

    }
}
