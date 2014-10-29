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

import com.eviware.soapui.model.testsuite.AssertionError;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

public class SoapValidatorTest {

    private static final String schemaPath = "src/test/resources/wsdl/affaire/AffaireServiceWrite.wsdl";

    private static SoapValidatorSoapUI validator;

    @BeforeClass
    public static void staticSetUp() {
        validator = new SoapValidatorSoapUI(schemaPath);
        validator.getOperationsQName();

    }

    @Test(expected = SoapException.class)
    public void loadDefinition() {
        new SoapValidatorSoapUI("");
    }

    @Test
    public void validateXml() {
        if (!validator.validateXml(request, null)) {
            fail("request is not valid xml.");
        }
    }

    @Test
    public void validateSoap() {
        SoapMessage requestMessage = validator.newRequestMessage(request);
        List<AssertionError> errors = new ArrayList<AssertionError>();

        if (!validator.validateXml(requestMessage.getRequestContent(), errors)) {
            fail("Request message is not valid xml");
        }

        /*
         * Request message validation
         */
        if (!validator.validateRequest(requestMessage, errors)) {
            fail("Request message not valid");
        }

        /*
         * Response message validation
         */
        requestMessage = requestMessage.setResponseContent(response);
        if (!validator.validateResponse(requestMessage, errors)) {
            fail("Response message not valid");
        }
    }

    @Test
    public void validateSoapFault() {
        SoapMessage requestMessage = validator.newRequestMessage(request);
        List<AssertionError> errors = new ArrayList<AssertionError>();

        /*
         * Response message validation
         */
        requestMessage = requestMessage.setResponseContent(fault);
        if (!validator.validateResponse(requestMessage, errors)) {
            fail("Response message not valid");
        }
    }

    String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v6=\"http://nsge.erdf.fr/sgel/aff/echange/service/v6_1\" xmlns:crtype=\"http://nsge.erdf.fr/SGEL/AFF/echange/types/v3\">\r\n"
            + "   <soapenv:Header/>\r\n"
            + "   <soapenv:Body>\r\n"
            + "      <v6:solderEtCloreAffairePrestationGDP>\r\n"
            + "         <contexte>\r\n"
            + "            <systemeId>?</systemeId>\r\n"
            + "         </contexte>\r\n"
            + "         <affaire>\r\n"
            + "            <affaireIdentifiant>00000050</affaireIdentifiant>\r\n"
            + "            <!--1 or more repetitions:-->\r\n"
            + "            <natureEvenements>CFN</natureEvenements>\r\n"
            + "            <prestation>\r\n"
            + "               <siContractuel>COSY</siContractuel>\r\n"
            + "               <prmIdentifiant>00000000000015</prmIdentifiant>\r\n"
            + "               <rang>2</rang>\r\n"
            + "               <realisation xsi:type=\"crtype:PrestationNonRealiseeType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
            + "                  <motif>motifNREA</motif>\r\n"
            + "                  <commentaire>commentaireNREA</commentaire>\r\n"
            + "               </realisation>\r\n"
            + "               <facturation xsi:type=\"crtype:PrestationNonFactureeType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
            + "                  <motif>motifNFAC</motif>\r\n"
            + "                  <commentaire>commentaireNFAC</commentaire>\r\n"
            + "               </facturation>\r\n"
            + "               <!--Optional:-->\r\n"
            + "               <referencePoint>referencePoint2</referencePoint>\r\n"
            + "               <!--Optional:-->\r\n"
            + "               <typePoint>PADT</typePoint>\r\n"
            + "            </prestation>\r\n"
            + "         </affaire>\r\n"
            + "      </v6:solderEtCloreAffairePrestationGDP>\r\n"
            + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

    String response = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
            + "   <S:Body>\r\n"
            + "      <ns2:solderEtCloreAffairePrestationGDPResponse xmlns:ns4=\"http://nsge.erdf.fr/sgel/affaire/echange/service/v2\" xmlns:ns3=\"http://nsge.erdf.fr/sgel/framework/echange/types/v1\" xmlns:ns2=\"http://nsge.erdf.fr/sgel/aff/echange/service/v6_1\"/>\r\n"
            + "   </S:Body>\r\n" + "</S:Envelope>";

    String fault = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
            + "   <S:Body>\r\n"
            + "      <S:Fault xmlns:ns4=\"http://www.w3.org/2003/05/soap-envelope\">\r\n"
            + "         <faultcode>S:Server</faultcode>\r\n"
            + "         <faultstring>erreur</faultstring>\r\n"
            + "         <detail>\r\n"
            + "            <ns3:error xmlns:ns4=\"http://nsge.erdf.fr/sgel/affaire/echange/service/v2\" xmlns:ns2=\"http://nsge.erdf.fr/sgel/aff/echange/service/v6_1\" xmlns:ns3=\"http://nsge.erdf.fr/sgel/framework/echange/types/v1\">\r\n"
            + "               <code>SGEL.PRM.ERR-F0033</code>\r\n"
            + "               <libelle>Le point n'existe pas.</libelle>\r\n"
            + "               <type>fonctionnelle</type>\r\n"
            + "               <idException>37212b87:1416d4b7aba:-7fde</idException>\r\n"
            + "               <codeNatif/>\r\n"
            + "            </ns3:error>\r\n"
            + "         </detail>\r\n"
            + "      </S:Fault>\r\n" + "   </S:Body>\r\n" + "</S:Envelope>";
}
