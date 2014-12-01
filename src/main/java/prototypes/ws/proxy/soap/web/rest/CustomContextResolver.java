/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes.ws.proxy.soap.web.rest;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.exchange.SoapExchange;

@Provider
@Produces({"application/xml", "application/json"})
public class CustomContextResolver implements ContextResolver<JAXBContext> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory
            .getLogger(NameGenerator.class);

    private JAXBContext jc;

    /**
     *
     * might use JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME =
     * "eclipselink.json.wrapper-as-array-name";
     */
    public CustomContextResolver() throws JAXBException {
        LOGGER.debug("CUSTOM RESOLVER LOADED");
        try {
            Map<String, Object> props = new HashMap<String, Object>(1);
            props.put(JAXBContextProperties.OXM_METADATA_SOURCE,
                    SoapExchange.class.getPackage().getName().replace(".", "/") + "/oxm.xml");
            jc = JAXBContext.newInstance(new Class[]{SoapExchange.class}, props);
        } catch (JAXBException ex) {
            LOGGER.warn("JAXBContext instantiation failed : {}", ex.getMessage());
            throw ex;
        }
    }

    public JAXBContext getContext(Class<?> clazz) {
        if (SoapExchange.class == clazz) {
            return jc;
        }
        try {
            return JAXBContext.newInstance(clazz);
        } catch (JAXBException ex) {
            LOGGER.warn("JAXBContext instantiation failed : {}", ex);
        }
        return null;
    }

}
