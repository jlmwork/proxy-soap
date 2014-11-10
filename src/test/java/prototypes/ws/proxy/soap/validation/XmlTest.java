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

import com.eviware.soapui.support.xml.XmlUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.junit.Test;

/**
 *
 * @author jlamande
 */
public class XmlTest {

    @Test
    public void test() {
        String xPath = "declare namespace env='http://schemas.xmlsoap.org/soap/envelope/'; declare namespace flt='';declare namespace ns='http://nsge.erdf.fr/sgel/framework/echange/types/v1';$this/flt:detail/ns:error";
        String xmlStr = "<xml-fragment xmlns:ns4=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "  <faultcode>S:Server</faultcode>\n"
                + "  <faultstring>erreur</faultstring>\n"
                + "  <detail>\n"
                + "    <ns3:error xmlns:ns4=\"http://nsge.erdf.fr/sgel/affaire/echange/service/v2\" xmlns:ns2=\"http://nsge.erdf.fr/sgel/aff/echange/service/v6_1\" xmlns:ns3=\"http://nsge.erdf.fr/sgel/framework/echange/types/v1\">\n"
                + "      <code>SGEL.PRM.ERR-F0033</code>\n"
                + "      <libelle>Le point n'existe pas.</libelle>\n"
                + "      <type>fonctionnelle</type>\n"
                + "      <idException>37212b87:1416d4b7aba:-7fde</idException>\n"
                + "      <codeNatif/>\n"
                + "    </ns3:error>\n"
                + "  </detail>\n"
                + "</xml-fragment>";
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setLoadLineNumbers();
        xmlOptions.setLoadLineNumbers("LOAD_LINE_NUMBERS_END_ELEMENT");
        try {
            XmlObject xml = XmlUtils.createXmlObject(xmlStr, xmlOptions);
            System.out.println("Try xPath");
            XmlObject paths[] = xml.selectPath(xPath);
            System.out.println(paths[0]);
        } catch (XmlException ex) {
            System.out.println(ex);
        }
    }
}
