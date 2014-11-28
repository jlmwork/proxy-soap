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

import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.submit.WsdlMessageExchange;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlUtils;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlValidator;
import com.eviware.soapui.support.xml.XmlUtils;
import com.ibm.wsdl.PartImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingOperation;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import prototypes.ws.proxy.soap.constants.Messages;
import prototypes.ws.proxy.soap.reflect.Classes;

public class ExtendedWsdlValidator extends WsdlValidator {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExtendedWsdlValidator.class);

    WsdlContext wsdlContext;

    static {
        // unlock private parent methods to avoid copying them in this class
        // convertErrors
        Classes.setMethodAccessible(WsdlValidator.class, "convertErrors",
                new Class<?>[]{List.class});
        // validateMessageBody
        Classes.setMethodAccessible(
                WsdlValidator.class,
                "validateMessageBody",
                new Class<?>[]{List.class, SchemaType.class, XmlObject.class});
    }

    public ExtendedWsdlValidator(WsdlContext wsdlContext) {
        super(wsdlContext);
        this.wsdlContext = wsdlContext;
    }

    /**
     *
     * TODO : check if found headers were expected in operation definition
     * BindingOperation bindingOperation = operation.getBindingOperation();
     * WsdlUtils.getInputParts(bindingOperation);
     *
     * List<WsdlUtils.SoapHeader> list = WsdlUtils
     * .getSoapHeaders(bindingOperation.getBindingInput()
     * .getExtensibilityElements()); for (WsdlUtils.SoapHeader header : list) {
     * QName typeName = header.getMessage();
     *
     * }
     *
     * @param requestMessage
     * @return
     */
    public List<String> assertHeaders(WsdlMessageExchange requestMessage) {
        List<String> errors = new ArrayList<String>();
        try {
            LOGGER.debug("Headers validation");
            requestMessage.getOperation();

            NodeList nodes = ((SoapMessage) requestMessage).getHeaders();
            List<XmlError> errorsH = new ArrayList<XmlError>();
            List<QName> foundHeaders = new ArrayList<QName>();

            if ((nodes != null) && (nodes.getLength() > 0)) {
                Node header = nodes.item(0);
                if (header != null) {
                    Node firstChild = header.getFirstChild();
                    if (firstChild != null) {
                        XmlObject x = XmlObject.Factory.parse(firstChild);
                        String strY = x.toString().replaceAll(
                                firstChild.getPrefix() + ":"
                                + firstChild.getLocalName(),
                                "xml-fragment");
                        XmlObject y = XmlObject.Factory.parse(strY);
                        Part part = new PartImpl();
                        QName headerQName = new QName(firstChild
                                .getNamespaceURI(), firstChild
                                .getLocalName(), "");
                        part.setElementName(headerQName);
                        foundHeaders.add(headerQName);
                        SchemaType typeH = WsdlUtils.getSchemaTypeForPart(wsdlContext,
                                part);
                        validateMessageBody(errorsH, typeH, y);
                    }
                }
            }

            if (!errorsH.isEmpty()) {
                for (XmlError error : errorsH) {
                    errors.add(error.toString());
                }
            }
            return errors;

        } catch (XmlException ex) {
            LOGGER.warn(Messages.MSG_ERROR_ON, " Headers Validation - XmlException ", ex.getMessage());
            LOGGER.debug(Messages.MSG_ERROR_DETAILS, ex);
        } catch (Exception ex) {
            LOGGER.warn(Messages.MSG_ERROR_ON, " Headers Validation - Exception", ex.getMessage());
            LOGGER.debug(Messages.MSG_ERROR_DETAILS, ex);
        }
        return errors;
    }

    private void validateMessageBody(List<XmlError> errors, SchemaType type,
            XmlObject msg) throws XmlException {
        Classes.callPrivateMethod(WsdlValidator.class, "validateMessageBody",
                this, new Class<?>[]{List.class, SchemaType.class,
                    XmlObject.class}, new Object[]{errors, type, msg});
    }

    public boolean validateSoapFaults(WsdlMessageExchange responseMessage, WsdlContext wsdlContext, List<String> errsF) {
        if (errsF == null) {
            errsF = new ArrayList<String>();
        }
        boolean foundFault = false;
        List<XmlError> xmlErrors = new ArrayList<XmlError>();
        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadLineNumbers("LOAD_LINE_NUMBERS_END_ELEMENT");
            xmlOptions.setErrorListener(xmlErrors);
            XmlObject xml;
            xml = XmlUtils.createXmlObject(responseMessage.getResponseContent(), xmlOptions);
            String xPath = (new StringBuilder()).append("declare namespace env='")
                    .append(wsdlContext.getSoapVersion().getEnvelopeNamespace())
                    .append("';")
                    .append("$this/env:Envelope/env:Body/env:Fault")
                    .toString();
            XmlObject[] paths = xml.selectPath(xPath);
            if (paths.length == 0) {
                LOGGER.debug("No Fault found");
            } else if (paths.length == 1) {
                foundFault = true;
                LOGGER.debug("Found a fault in message so will validate it");
                WsdlOperation operation = responseMessage.getOperation();
                BindingOperation bindingOperation = operation.getBindingOperation();
                validateSoapFault(wsdlContext, bindingOperation, paths[0], xmlErrors);
            } else {
                errsF.add("Too many faults found in message");
            }
        } catch (XmlException ex) {
            LOGGER.debug("Error validating faults ", ex);
        } catch (Exception ex) {
            LOGGER.debug("Error validating faults ", ex);
        }
        for (XmlError xmlError : xmlErrors) {
            errsF.add(xmlError.toString());
        }
        return foundFault;
    }

    private void validateSoapFault(WsdlContext wsdlContext, BindingOperation bindingOperation, XmlObject msgXml, List errors)
            throws ValidationException {
        try {
            Map faults = bindingOperation.getBindingFaults();

            LOGGER.debug("Search Fault Type among WSDL Operation Binding Fault Parts : {} ", faults.size());
            if (faults.size() > 0) {
                Iterator<BindingFault> i = faults.values().iterator();
                while (i.hasNext()) {
                    BindingFault bindingFault = i.next();
                    String faultName = bindingFault.getName();

                    Part[] faultParts = WsdlUtils.getFaultParts(bindingOperation, faultName);
                    if (faultParts.length == 0) {
                        LOGGER.warn("Missing fault parts in wsdl for fault [{}] in bindingOperation [{}]", faultName, bindingOperation.getName());
                        continue;
                    }

                    if (faultParts.length != 1) {
                        LOGGER.info("Too many fault parts in wsdl for fault [{}] in bindingOperation [{}]", faultName, bindingOperation.getName());
                        continue;
                    }

                    Part part = faultParts[0];
                    QName elementName = part.getElementName();
                    LOGGER.debug("Binding Fault found in WSDL : {}", elementName);

                    if (elementName != null) {
                        String faultXPath = "declare namespace env='"
                                + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "'; declare namespace flt='"
                                + wsdlContext.getSoapVersion().getFaultDetailNamespace() + "';" + "declare namespace ns='"
                                + elementName.getNamespaceURI() + "';" + "$this/flt:detail/ns:" + elementName.getLocalPart();
                        LOGGER.debug("XML : {}", msgXml);
                        LOGGER.debug("Fault XPath : {}", faultXPath);
                        XmlObject[] paths = msgXml.selectPath(faultXPath);
                        LOGGER.debug("Found Fault XPaths : ", paths.length);

                        if (paths.length == 1) {
                            LOGGER.debug("Found Fault");
                            SchemaGlobalElement elm = wsdlContext.getSchemaTypeLoader().findElement(elementName);
                            if (elm != null) {
                                validateMessageBody(errors, elm.getType(), paths[0]);
                            } else {
                                errors.add(XmlError.forMessage("Missing fault part element [" + elementName + "] for fault ["
                                        + part.getName() + "] in associated schema"));
                            }

                            return;
                        }
                    } else if (part.getTypeName() != null) {
                        // this is not allowed by Basic Profile.. remove?
                        QName typeName = part.getTypeName();

                        XmlObject[] paths = msgXml.selectPath("declare namespace env='"
                                + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "'; declare namespace flt='"
                                + wsdlContext.getSoapVersion().getFaultDetailNamespace() + "';" + "declare namespace ns='"
                                + typeName.getNamespaceURI() + "';" + "$this/flt:detail/ns:" + part.getName());

                        if (paths.length == 1) {
                            SchemaType type = wsdlContext.getSchemaTypeLoader().findType(typeName);
                            if (type != null) {
                                validateMessageBody(errors, type, paths[0]);
                            } else {
                                errors.add(XmlError.forMessage("Missing fault part type [" + typeName + "] for fault ["
                                        + part.getName() + "] in associated schema"));
                            }

                            return;
                        }
                    }
                }

                // if we get here, no matching fault was found.. this is not an error but
                // should be warned..
                String noMatchXPath = "declare namespace env='"
                        + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "'; declare namespace flt='"
                        + wsdlContext.getSoapVersion().getFaultDetailNamespace() + "';$this/flt:detail";
                LOGGER.debug("XML : {}", msgXml);
                LOGGER.debug("Fault XPath : {}", noMatchXPath);
                XmlObject[] paths = msgXml.selectPath(noMatchXPath);

                if (paths.length == 0) {
                    LOGGER.warn("Missing matching Fault in wsdl for bindingOperation [{}]", bindingOperation.getName());
                    errors.add(XmlError.forMessage("Missing matching Fault in wsdl for bindingOperation in associated schema"));
                } else {
                    String xmlText = paths[0].xmlText(new XmlOptions().setSaveOuter());
                    LOGGER.warn("Missing matching Fault in wsdl for Fault Detail element [{}] in bindingOperation [{}]",
                            XmlUtils.removeUnneccessaryNamespaces(xmlText), bindingOperation.getName());
                    errors.add(XmlError.forMessage("Missing fault part element [" + XmlUtils.removeUnneccessaryNamespaces(xmlText) + "] for fault ["
                            + bindingOperation.getName() + "] in associated schema"));
                }
            } else {
                // no special fault has been defined on this operation.
                // so we wont do any validation
            }
        } catch (XmlException ex) {
            throw new ValidationException(ex);
        } catch (Exception ex) {
            throw new ValidationException(ex);
        }
    }

}
