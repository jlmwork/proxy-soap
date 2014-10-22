package prototypes.ws.proxy.soap.validation;

import java.io.IOException;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import prototypes.ws.proxy.soap.constantes.ProxyErrorConstantes;
import prototypes.ws.proxy.soap.constantes.SoapConstantes;
import prototypes.ws.proxy.soap.xml.XmlUtils;

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.submit.WsdlMessageExchange;
import com.eviware.soapui.impl.wsdl.support.soap.SoapVersion;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.iface.Attachment;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.soapui.support.types.StringToStringsMap;

public class SoapMessage implements WsdlMessageExchange {

    private String        request, response;
    private String        operationName;
    private WsdlInterface wsdlInterface;
    private WsdlOperation wsdlOperation;

    public SoapMessage(String requestBody, WsdlInterface wsdlInterface)
            throws SAXException, IOException {

        if ((requestBody == null) || (requestBody.length() == 0)) {
            throw new SoapException(ProxyErrorConstantes.EMPTY_REQUEST);
        }

        this.request = requestBody;
        this.wsdlInterface = wsdlInterface;

        Node opNode = XmlUtils.firstChild(request, SoapConstantes.BODY);

        /*
         * No body, or no body's children
         */
        if (opNode == null) {
            return;
        }

        operationName = opNode.getLocalName();
        wsdlOperation = wsdlInterface.getOperationByName(operationName);
    }

    public NodeList getHeaders() throws SAXException, IOException {
        return XmlUtils.children(request, SoapConstantes.HEADER);
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

        return new Attachment[] {};
    }

    @Override
    public Attachment[] getRequestAttachmentsForPart(String arg0) {

        return new Attachment[] {};
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

        return new Attachment[] {};
    }

    @Override
    public Attachment[] getResponseAttachmentsForPart(String arg0) {

        return new Attachment[] {};
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
