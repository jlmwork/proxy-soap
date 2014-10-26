package prototypes.ws.proxy.soap.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prototypes.ws.proxy.soap.reflect.Classes;

import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;

public class ClassesTest {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ClassesTest.class);

    public static void main(String... args) {
        WsdlContext context = new WsdlContext(
                "src/test/resources/wsdl/affaire/AffaireServiceWrite.wsdl");
        Classes.setStaticField(WsdlContext.class.getSuperclass(),
                "definitionCache", null);
        LOGGER.debug("done");
    }

}
