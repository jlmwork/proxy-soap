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
package prototypes.ws.proxy.soap.validation;

import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.support.definition.InterfaceDefinition;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.submit.WsdlMessageExchange;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlInterfaceDefinition;
import com.eviware.soapui.model.testsuite.AssertionError;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import prototypes.ws.proxy.soap.constantes.SoapErrorConstantes;
import prototypes.ws.proxy.soap.reflect.Classes;
import prototypes.ws.proxy.soap.web.io.Requests;
import prototypes.ws.proxy.soap.xml.XmlStrings;

public class SoapValidatorSoapUI implements SoapValidator {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SoapValidatorSoapUI.class);

    private final String id;

    private final String from;

    private final Long createdTime;

    private final String schemaPath;

    private WsdlInterface wsdlInterface;

    private ExtendedWsdlValidator wsdlValidator;

    private List<QName> operationsQname = new ArrayList<QName>();

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

    @Override
    public List<QName> getOperationsQName() {
        return operationsQname;
    }

    /**
     * Load or reload definition from wsdl file.
     */
    private void loadDefinition() {
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

            Map<QName, Message> vars = (Map<QName, Message>) this.wsdlInterface.getDefinitionContext().getDefinition().getMessages();
            for (QName qname : vars.keySet()) {
                Message mess = vars.get(qname);
                Map<String, Part> parts = mess.getParts();
                for (Part qNamePart : parts.values()) {
                    operationsQname.add(qNamePart.getElementName());
                    LOGGER.debug("WSDL contains Part {}", qNamePart.getElementName());
                }
            }

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
    public boolean validateXml(String xml, List<String> errors) {
        List<String> errs = XmlStrings.validateXml(xml);
        return errs.isEmpty();
    }

    @Override
    public boolean validateRequest(WsdlMessageExchange requestMessage,
            List<String> errors) {
        LOGGER.debug("Request validation");
        // XML Validation
        if (!validateXml(requestMessage.getRequestContent(), errors)) {
            return false;
        }

        LOGGER.debug("Request body validation");
        // this asserts only the soap body
        AssertionError[] errs = wsdlValidator.assertRequest(requestMessage,
                false);

        if (errs != null) {
            for (AssertionError error : errs) {
                errors.add(error.toString());
            }
        }

        LOGGER.debug("Request headers validation");
        List<String> errsH = wsdlValidator.assertHeaders(requestMessage);
        if (errsH != null) {
            errors.addAll(errsH);
        }
        return errors.isEmpty();
    }

    @Override
    public boolean validateResponse(WsdlMessageExchange responseMessage) {
        return validateResponse(responseMessage, null);
    }

    @Override
    public boolean validateResponse(WsdlMessageExchange responseMessage,
            List<String> errors) {
        LOGGER.debug("Response validation");
        // XML Validation
        if (!validateXml(responseMessage.getResponseContent(), errors)) {
            return false;
        }

        // need to process custom soap faults validation as the one of SoapUI
        // is not well implemented
        List<String> errsF = new ArrayList();
        boolean foundFault = wsdlValidator.validateSoapFaults(responseMessage, this.wsdlInterface.getWsdlContext(), errsF);
        if (errsF.size() > 0) {
            LOGGER.debug("Errors on Response Fault validation");
            errors.addAll(errsF);
            return false;
        }

        // no fault so validate response
        if (!foundFault) {
            // Response validation with wsdl
            // this asserts only the soap body
            LOGGER.debug("Response body validation");
            AssertionError[] errs = wsdlValidator.assertResponse(responseMessage,
                    false);

            if (errs != null) {
                LOGGER.debug("Errors on Response body validation");
                for (AssertionError error : errs) {
                    errors.add(error.toString());
                }
            }
        }

        LOGGER.debug("Response headers validation");
        List<String> errsH = wsdlValidator.assertHeaders(responseMessage);
        if (errsH != null) {
            LOGGER.debug("Errors on Response headers validation");
            errors.addAll(errsH);
        }

        return errors.isEmpty();
    }

    @Override
    public SoapMessage newRequestMessage(String message) {
        try {
            return new SoapMessage(message, wsdlInterface);
        } catch (SoapException e) {
            throw e;
        } catch (IOException e) {
            throw new SoapException("Make request message fail", e);
        } catch (SAXException e) {
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
