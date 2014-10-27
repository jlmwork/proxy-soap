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

import com.eviware.soapui.impl.wsdl.submit.WsdlMessageExchange;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlUtils;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlValidator;
import com.eviware.soapui.model.testsuite.AssertionError;
import com.ibm.wsdl.PartImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import prototypes.ws.proxy.soap.reflect.Classes;

public class ExtendedWsdlValidator extends WsdlValidator {

    private static final Logger LOG = LoggerFactory
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

    public AssertionError[] assertHeaders(WsdlMessageExchange requestMessage) {
        try {
            LOG.debug("Headers validation");
            requestMessage.getOperation();

            NodeList nodes = ((SoapMessage) requestMessage).getHeaders();
            System.out.println(nodes);
            List<XmlError> errorsH = new ArrayList<XmlError>();
            List<QName> foundHeaders = new ArrayList<QName>();

            if ((nodes != null) && (nodes.getLength() > 0)) {
                Node header = nodes.item(0);
                XmlObject x = XmlObject.Factory.parse(header.getFirstChild());
                System.out.println(x);
                String strY = x.toString().replaceAll(
                        header.getFirstChild().getPrefix() + ":"
                        + header.getFirstChild().getLocalName(),
                        "xml-fragment");
                XmlObject y = XmlObject.Factory.parse(strY);
                Part part = new PartImpl();
                QName headerQName = new QName(header.getFirstChild()
                        .getNamespaceURI(), header.getFirstChild()
                        .getLocalName(), "");
                part.setElementName(headerQName);
                foundHeaders.add(headerQName);
                SchemaType typeH = WsdlUtils.getSchemaTypeForPart(wsdlContext,
                        part);
                validateMessageBody(errorsH, typeH, y);
            }

            // TODO : check if found headers were expected in operation
            // definition
            /*
             * BindingOperation bindingOperation =
             * operation.getBindingOperation();
             * WsdlUtils.getInputParts(bindingOperation);
             *
             * List<WsdlUtils.SoapHeader> list = WsdlUtils
             * .getSoapHeaders(bindingOperation.getBindingInput()
             * .getExtensibilityElements()); for (WsdlUtils.SoapHeader header :
             * list) { QName typeName = header.getMessage();
             *
             *
             * }
             */
            return _internalConvertErrors(errorsH);
        } catch (XmlException e) {
            LOG.warn("XMLError : " + e.getMessage());
        } catch (Exception e) {
            LOG.warn("Exception : " + e.getMessage());
        }
        return new AssertionError[0];
    }

    private void validateMessageBody(List<XmlError> errors, SchemaType type,
            XmlObject msg) throws XmlException {
        Classes.callPrivateMethod(WsdlValidator.class, "validateMessageBody",
                this, new Class<?>[]{List.class, SchemaType.class,
                    XmlObject.class}, new Object[]{errors, type, msg});
    }

    private AssertionError[] _internalConvertErrors(List<XmlError> errors) {
        return (AssertionError[]) Classes.callPrivateMethod(
                WsdlValidator.class, "convertErrors", this,
                new Class<?>[]{List.class}, new Object[]{errors});
    }

    public static AssertionError[] convertErrors(List<XmlError> errors) {
        if (errors.size() > 0) {
            List<AssertionError> response = new ArrayList<AssertionError>();
            for (Iterator<XmlError> i = errors.iterator(); i.hasNext();) {
                XmlError error = i.next();
                AssertionError assertionError = new AssertionError(error);
                if (!response.contains(assertionError)) {
                    response.add(assertionError);
                }
            }
            return response.toArray(new AssertionError[response.size()]);
        }
        return new AssertionError[0];
    }

}
