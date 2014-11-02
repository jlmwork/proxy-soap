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
import javax.xml.transform.ErrorListener;
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

public class XmlStrings {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlStrings.class);

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

        xmlContent = XmlStrings.cleanXmlRequest(xmlContent);
        Node xml = XmlStrings.parseXML(xmlContent);

        Document root = xml.getOwnerDocument();
        NodeList bodyNodes = root.getElementsByTagNameNS(SOAP_SCHEMA, tagName);

        if ((bodyNodes == null) || (bodyNodes.getLength() == 0)) {
            return null;
        }

        return bodyNodes.item(0);
    }

    public static NodeList children(String xmlContent, String tagName)
            throws SAXException, IOException {
        xmlContent = XmlStrings.cleanXmlRequest(xmlContent);
        Node xml = XmlStrings.parseXML(xmlContent);

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
            LOGGER.trace("create sax transformer");
            Transformer serializer = SAXTransformerFactory.newInstance()
                    .newTransformer();
            LOGGER.trace(serializer.getClass().getName());
            LOGGER.trace("set output prop");
            serializer.setErrorListener(new ErrorListener() {

                @Override
                public void warning(TransformerException exception) throws TransformerException {
                    LOGGER.warn(exception.getMessage());
                }

                @Override
                public void error(TransformerException exception) throws TransformerException {
                    LOGGER.error(exception.getMessage());
                }

                @Override
                public void fatalError(TransformerException exception) throws TransformerException {
                    LOGGER.error(exception.getMessage());
                }
            });
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            // serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
            // "yes");

            LOGGER.trace("set output prop");
            serializer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            // serializer.setOutputProperty("{http://xml.customer.org/xslt}indent-amount",
            // "2");

            LOGGER.trace("create sax source");
            Source xmlSource = new SAXSource(new InputSource(
                    new ByteArrayInputStream(xml.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());

            LOGGER.trace("sax transform");
            serializer.transform(xmlSource, res);

            LOGGER.trace("get res");
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

    public static XmlObject parseXml(String input, XmlOptions xmlOptions)
            throws XmlException {
        return XmlObject.Factory.parse(input, xmlOptions);
    }

    public static List<String> validateXml(String xml) {
        List<String> errs = new ArrayList<String>();
        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadLineNumbers();
            xmlOptions.setErrorListener(errs);
            xmlOptions
                    .setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);
            // XmlObject.Factory.parse( request, xmlOptions );
            XmlStrings.parseXml(xml, xmlOptions);
        } catch (XmlException e) {
            if (e.getErrors() != null) {
                LOGGER.debug("XML errors found : " + e.getErrors());
                for (Object error : e.getErrors()) {
                    errs.add(error.toString());
                }
            }
            errs.add(XmlError.forMessage(e.getMessage()).toString());
        } catch (Exception e) {
            errs.add(XmlError.forMessage(e.getMessage()).toString());
        }

        return errs;
    }
}
