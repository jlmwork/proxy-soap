package prototypes.ws.proxy.soap.validation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.xmlbeans.XmlError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import prototypes.ws.proxy.soap.constantes.SoapConstantes;
import prototypes.ws.proxy.soap.constantes.SoapErrorConstantes;
import prototypes.ws.proxy.soap.io.Requests;
import prototypes.ws.proxy.soap.reflect.Classes;
import prototypes.ws.proxy.soap.xml.XmlStrings;

import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.support.definition.InterfaceDefinition;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.submit.WsdlMessageExchange;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlInterfaceDefinition;
import com.eviware.soapui.model.testsuite.AssertionError;

public class SoapValidatorSoapUI implements SoapValidator {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SoapValidatorSoapUI.class);

    private final String id;

    private final String from;

    private final Long createdTime;

    private final String schemaPath;

    private WsdlInterface wsdlInterface;

    private ExtendedWsdlValidator wsdlValidator;

    public static final String FAULT = "Fault";

    public SoapValidatorSoapUI(String schemaPath) {
        this(schemaPath, "", "");
    }

    public SoapValidatorSoapUI(String schemaPath, String key, String from) {
        this.schemaPath = schemaPath;
        this.id = key;
        this.from = from;
        createdTime = Calendar.getInstance().getTimeInMillis();
        loadDefinition();
    }

    /**
     * Load or reload definition from wsdl file.
     */
    public void loadDefinition() {
        // Check file exists if it is not a remote resource
        if (!Requests.isHttpPath(schemaPath)) {
            if (!new File(schemaPath).exists()) {
                LOGGER.debug("File does not exists : " + schemaPath);
                throw new NotFoundSoapException(String.format(
                        SoapErrorConstantes.WSDL_NOT_FOUND, schemaPath));
            }
        }

        try {
            /*
             * // Load definition from wsdl file WsdlContext wsdlContext = new
             * WsdlContext(schemaPath); LOGGER.debug("DefintionCache : " +
             * wsdlContext.getDefinitionCache().getClass().getName());
             * LOGGER.debug("DefintionCache : " +
             * wsdlContext.getDefinitionCache()); wsdlContext.load();
             */
            // Import wsdl interface from definition
            WsdlProject project = new WsdlProject();
            project.setCacheDefinitions(false);
            WsdlInterface[] wsdlInterfaces = WsdlInterfaceFactory.importWsdl(
                    project, schemaPath, false);

            if (wsdlInterfaces.length == 0) {
                LOGGER.error("No wsdl interface");
                return;
            }

            wsdlInterface = wsdlInterfaces[0];

            // Make Wsdl Validator
            wsdlValidator = new ExtendedWsdlValidator(
                    wsdlInterface.getWsdlContext());

            // destroy timer
            // SoapUI.getSoapUITimer().cancel();
        } catch (Exception e) {
            LOGGER.error("WSDL: Load definition fail. Schema path : "
                    + schemaPath, e);
            throw new SoapException(
                    "WSDL: Load definition fail. Schema path : " + schemaPath,
                    e);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public Long getCreationTime() {
        return createdTime;
    }

    public static void clearCaches() {
        // try to cleanup SoapUI Definition Caches
        // clean SoapUI Definition Caches
        try {
            LOGGER.info("Clear SoapUI caches");
            Classes.setStaticField(WsdlContext.class.getSuperclass(),
                    "definitionCache",
                    new HashMap<String, InterfaceDefinition<?>>());
            Classes.setStaticField(WsdlContext.class.getSuperclass(),
                    "urlReferences", new HashMap<String, Integer>());
            Classes.setStaticField(WsdlInterfaceDefinition.class, "factory",
                    null);
            Classes.setStaticField(WsdlInterfaceDefinition.class, "wsdlReader",
                    null);

        } catch (Exception e) {
            LOGGER.warn("Error while clearing Cache", e);
        }
    }

    @Override
    public boolean validateXml(String xml, List<AssertionError> errors) {
        List<XmlError> errs = XmlStrings.validateXml(xml);
        if (errors == null) {
            errors = new ArrayList<AssertionError>();
        }

        for (XmlError error : errs) {
            errors.add(new AssertionError(error));
        }

        return errors.isEmpty();
    }

    @Override
    public boolean validateRequest(WsdlMessageExchange requestMessage,
            List<AssertionError> errors) {
        LOGGER.debug("Request validation");
        // XML Validation
        if (!validateXml(requestMessage.getRequestContent(), errors)) {
            return false;
        }

        LOGGER.debug("Request body validation");
        // this asserts only the soap body
        AssertionError[] errs = wsdlValidator.assertRequest(requestMessage,
                false);

        if (errors != null) {
            errors.addAll(Arrays.asList(errs));
        }

        LOGGER.debug("Request headers validation");
        AssertionError[] errsH = wsdlValidator.assertHeaders(requestMessage);
        if (errsH != null) {
            errors.addAll(Arrays.asList(errsH));
        }
        return errors.size() == 0;
    }

    @Override
    public boolean validateResponse(WsdlMessageExchange responseMessage) {
        return validateResponse(responseMessage, null);
    }

    @Override
    public boolean validateResponse(WsdlMessageExchange responseMessage,
            List<AssertionError> errors) {
        LOGGER.debug("Response validation");
        // XML Validation
        if (!validateXml(responseMessage.getResponseContent(), errors)) {
            return false;
        }
        // Fault test
        Node opNode;
        try {
            opNode = XmlStrings.firstChild(responseMessage.getResponseContent(),
                    SoapConstantes.BODY);
        } catch (Exception e) {
            LOGGER.error("Response get body first child fail", e);
            return false;
        }

        // No body, or no body's children
        if (opNode == null) {
            return false;
        }

        // Response is fault, validation ok
        if (FAULT.equals(opNode.getLocalName())) {
            return true;
        }

        // Response validation with wsdl
        // this asserts only the soap body
        LOGGER.debug("Response body validation");
        AssertionError[] errs = wsdlValidator.assertResponse(responseMessage,
                false);

        if (errors != null) {
            errors.addAll(Arrays.asList(errs));
        }

        LOGGER.debug("Response headers validation");
        AssertionError[] errsH = wsdlValidator.assertHeaders(responseMessage);
        errors.addAll(Arrays.asList(errsH));

        return errors.size() == 0;
    }

    @Override
    public SoapMessage newRequestMessage(String message) {
        try {
            return new SoapMessage(message, wsdlInterface);
        } catch (SoapException e) {
            throw e;
        } catch (Exception e) {
            throw new SoapException("Make request message fail", e);
        }
    }

    @Override
    public SoapMessage newResponseMessage(String message,
            SoapMessage requestMessage) {
        return requestMessage.setResponseContent(message);
    }

    @Override
    public Object[] getOperations() {
        if (((this.wsdlInterface != null) && (this.wsdlInterface
                .getOperations() != null))) {
            Object[] opes = this.wsdlInterface.getOperations().keySet()
                    .toArray();
            Arrays.sort(opes);
            return opes;
        }
        return new Object[0];

    }

    @Override
    public String getUrl() {
        if ((this.wsdlInterface != null)
                && (this.wsdlInterface.getWsdlContext() != null)) {
            return this.wsdlInterface.getWsdlContext().getUrl();
        }
        return "";
    }
}
