/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes.ws.proxy.soap.web.rest;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import prototypes.ws.proxy.soap.model.SoapExchange;

//@Provider
//@Produces({"application/xml", "application/json"})
public class CustomContextResolver implements ContextResolver<JAXBContext> {

    private JAXBContext jc;

    public CustomContextResolver() {
        System.out.println("CUSTOM RESOLVER LOADED");
        try {
            Map<String, Object> props = new HashMap<String, Object>(1);
            props.put(JAXBContextProperties.OXM_METADATA_SOURCE, SoapExchange.class.getPackage().getName().replace(".", "/") + "/oxm.xml");
            //  JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME = "eclipselink.json.wrapper-as-array-name";
            jc = JAXBContext.newInstance(new Class[]{SoapExchange.class}, props);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public JAXBContext getContext(Class<?> clazz) {
        if (SoapExchange.class == clazz) {
            return jc;
        }
        return null;
    }

}
