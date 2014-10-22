package prototypes.ws.proxy.soap.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);

    private static String SOAP_SCHEMA = "http://schemas.xmlsoap.org/soap/envelope/";

    /**
     * Removes blanks between tags.
     *
     * @param request
     * @return
     */
    public static String cleanXmlRequest(String request) {
        String res = request;
        res = res.trim();
        res = res.replaceAll(">(\\W)*<", "><").replaceAll("\\n?\\r?", "");
        return res;
    }

    /**
     * Convert node object to string.
     *
     * @param node
     * @return
     * @throws TransformerException
     */
    public static String nodeToString(Node node) throws TransformerException {
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(node);
        trans.transform(source, result);
        String content = sw.toString();
        return content;
    }

    /**
     * Convert xml content to Node object.
     *
     * @param xmlContent
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static Node parseXML(String xmlContent) throws SAXException,
            IOException {
        ByteArrayInputStream bAIS = new ByteArrayInputStream(
                xmlContent.getBytes());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        factory.setNamespaceAware(true);

        try {
            docBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(
                    "Failed to initialize XML document builder", e);
        }

        Document document = docBuilder.parse(bAIS);
        Element root = document.getDocumentElement();
        return root;
    }

    /**
     * Get first element tagName in xmlContent.
     *
     * @param xmlContent
     * @param tagName
     * @return
     * @throws SAXException
     * @throws IOException
     */
    public static Node first(String xmlContent, String tagName)
            throws SAXException, IOException {

        xmlContent = XmlUtils.cleanXmlRequest(xmlContent);
        Node xml = XmlUtils.parseXML(xmlContent);

        Document root = xml.getOwnerDocument();
        NodeList bodyNodes = root.getElementsByTagNameNS(SOAP_SCHEMA, tagName);

        if ((bodyNodes == null) || (bodyNodes.getLength() == 0)) {
            return null;
        }

        return bodyNodes.item(0);
    }

    public static NodeList children(String xmlContent, String tagName)
            throws SAXException, IOException {
        xmlContent = XmlUtils.cleanXmlRequest(xmlContent);
        Node xml = XmlUtils.parseXML(xmlContent);

        Document root = xml.getOwnerDocument();
        NodeList bodyNodes = root.getElementsByTagNameNS(SOAP_SCHEMA, tagName);

        if ((bodyNodes == null) || (bodyNodes.getLength() == 0)) {
            return null;
        }

        return bodyNodes;
    }

    /**
     * Get first child of first element tagName in xmlContent.
     *
     * @param xmlContent
     * @param tagName
     * @return
     * @throws SAXException
     * @throws IOException
     */
    public static Node firstChild(String xmlContent, String tagName)
            throws SAXException, IOException {

        Node first = first(xmlContent, tagName);

        if ((first == null) || (first.getChildNodes().getLength() == 0)) {
            return null;
        }

        return first.getChildNodes().item(0);
    }

    /**
     * Format XML.
     *
     * @param unformattedXml
     * @return
     */
    public static String format(String xml) {
        if (xml == null) {
            return null;
        }

        try {
            xml = cleanXmlRequest(xml);

            Transformer serializer = SAXTransformerFactory.newInstance()
                    .newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            // serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
            // "yes");
            serializer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            // serializer.setOutputProperty("{http://xml.customer.org/xslt}indent-amount",
            // "2");
            Source xmlSource = new SAXSource(new InputSource(
                    new ByteArrayInputStream(xml.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            return new String(
                    ((ByteArrayOutputStream) res.getOutputStream())
                    .toByteArray());
        } catch (IllegalArgumentException e) {
            LOGGER.debug("XmlUtils.format Error : " + e.getMessage());
        } catch (TransformerException e) {
            LOGGER.debug("XmlUtils.format Error : " + e.getMessage());
            LOGGER.debug("XML Message was : " + xml);
        }
        return xml;
    }

    public static XmlObject createXmlObject(String input, XmlOptions xmlOptions)
            throws XmlException {
        return XmlObject.Factory.parse(input, xmlOptions);
    }

    public static List<XmlError> validateXml(String xml) {
        List<XmlError> errs = new ArrayList<XmlError>();
        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadLineNumbers();
            xmlOptions.setErrorListener(errs);
            xmlOptions
                    .setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);
            // XmlObject.Factory.parse( request, xmlOptions );
            XmlUtils.createXmlObject(xml, xmlOptions);
        } catch (XmlException e) {
            if (e.getErrors() != null) {
                errs.addAll(e.getErrors());
            }
            errs.add(XmlError.forMessage(e.getMessage()));
        } catch (Exception e) {
            errs.add(XmlError.forMessage(e.getMessage()));
        }

        return errs;
    }
}
