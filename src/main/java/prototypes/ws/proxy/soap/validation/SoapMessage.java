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

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.submit.WsdlMessageExchange;
import com.eviware.soapui.impl.wsdl.support.soap.SoapVersion;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.iface.Attachment;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.soapui.support.types.StringToStringsMap;
import java.io.IOException;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import prototypes.ws.proxy.soap.constantes.ProxyErrorConstantes;
import prototypes.ws.proxy.soap.constantes.SoapConstantes;
import prototypes.ws.proxy.soap.xml.XmlStrings;

public class SoapMessage implements WsdlMessageExchange {

    private String request, response;
    private QName operationName;
    private WsdlInterface wsdlInterface;
    private WsdlOperation wsdlOperation;

    public SoapMessage(String requestBody, WsdlInterface wsdlInterface, QName operationQName)
            throws SAXException, IOException {
        if ((requestBody == null) || (requestBody.length() == 0)) {
            throw new SoapException(ProxyErrorConstantes.EMPTY_REQUEST);
        }
        this.request = requestBody;
        this.wsdlInterface = wsdlInterface;
        // No body, or no body's children
        if (operationQName == null) {
            return;
        }
        operationName = operationQName;
        wsdlOperation = wsdlInterface.getOperationByName(operationName.getLocalPart());
    }

    public SoapMessage(String requestBody, WsdlInterface wsdlInterface)
            throws SAXException, IOException {
        this(requestBody, wsdlInterface, getOperationNameFromBody(requestBody));

    }

    public static QName getOperationNameFromBody(String body) throws SAXException, IOException {
        if ((body == null) || (body.length() == 0)) {
            throw new SoapException(ProxyErrorConstantes.EMPTY_REQUEST);
        }

        Node opNode = XmlStrings.firstChild(body, SoapConstantes.BODY);

        // No body, or no body's children
        if (opNode == null) {
            return null;
        }

        QName qname = new QName(opNode.getNamespaceURI(), opNode.getLocalName());
        return qname;
    }

    public NodeList getHeaders() throws SAXException, IOException {
        return XmlStrings.children(request, SoapConstantes.HEADER);
    }

    @Override
    public String getResponseContentType() {

        return null;
    }

    @Override
    public int getResponseStatusCode() {

        return 0;
    }

    @Override
    public String getEndpoint() {

        return null;
    }

    @Override
    public String[] getMessages() {

        return null;
    }

    @Override
    public ModelItem getModelItem() {

        return null;
    }

    @Override
    public StringToStringMap getProperties() {

        return null;
    }

    @Override
    public String getProperty(String arg0) {

        return null;
    }

    @Override
    public byte[] getRawRequestData() {

        return null;
    }

    @Override
    public byte[] getRawResponseData() {

        return null;
    }

    @Override
    public Attachment[] getRequestAttachments() {

        return new Attachment[]{};
    }

    @Override
    public Attachment[] getRequestAttachmentsForPart(String arg0) {

        return new Attachment[]{};
    }

    @Override
    public String getRequestContent() {
        return request;
    }

    @Override
    public String getRequestContentAsXml() {
        return request;
    }

    @Override
    public StringToStringsMap getRequestHeaders() {

        return null;
    }

    @Override
    public Attachment[] getResponseAttachments() {

        return new Attachment[]{};
    }

    @Override
    public Attachment[] getResponseAttachmentsForPart(String arg0) {

        return new Attachment[]{};
    }

    public SoapMessage setResponseContent(String response) {
        this.response = response;
        return this;
    }

    @Override
    public String getResponseContent() {

        return response;
    }

    @Override
    public String getResponseContentAsXml() {

        return response;
    }

    @Override
    public StringToStringsMap getResponseHeaders() {

        return null;
    }

    @Override
    public long getTimeTaken() {

        return 0;
    }

    @Override
    public long getTimestamp() {

        return 0;
    }

    @Override
    public boolean hasRawData() {

        return false;
    }

    @Override
    public boolean hasRequest(boolean arg0) {
        return request != null;
    }

    @Override
    public boolean hasResponse() {
        return response != null;
    }

    @Override
    public boolean isDiscarded() {

        return false;
    }

    @Override
    public WsdlOperation getOperation() {
        return wsdlOperation;
    }

    @Override
    public Vector<?> getRequestWssResult() {

        return null;
    }

    @Override
    public Vector<?> getResponseWssResult() {

        return null;
    }

    @Override
    public SoapVersion getSoapVersion() {
        return wsdlInterface.getSoapVersion();
    }
}
