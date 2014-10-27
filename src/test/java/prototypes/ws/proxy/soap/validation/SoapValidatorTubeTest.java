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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import prototypes.ws.proxy.soap.io.Streams;

public class SoapValidatorTubeTest {

    private final String schemaPath = "src/test/resources/TUBE-1.3.0-SGEL-20140129/Tube/Exposition/PropagationDonneesPointEtAffaire/Signature/ERDF.TUBE.EXT.PropagationDonneesPointEtAffaire-ws-v2.PORTAIL_SGEL.wsdl";
    private SoapValidatorSoapUI validator;

    @Before
    public void setUp() throws IOException {
        validator = new SoapValidatorSoapUI(schemaPath);
    }

    @Test
    public void validateXml() throws IOException {
        // String request = new
        // String(ByteStreams.toByteArray(this.getClass().getClassLoader().getResourceAsStream("sample-soaps/propagerDonneesPointEtAffaire.xml")));
        // if (!validator.validateXml(request, null))
        // fail("request is not valid xml.");
    }

    @Test
    public void validateSoap() throws IOException {
        String request = Streams.getString(this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(
                        "sample-soaps/propagerDonneesPointEtAffaire_OK.xml"));
        SoapMessage requestMessage = validator.newRequestMessage(request);
        List<AssertionError> errors = new ArrayList<AssertionError>();
        if (!validator.validateRequest(requestMessage, errors)) {
            System.out.println(errors);
            fail("Request message not valid");
        }
    }

    @Test
    public void unvalidateSoapHeaders() throws IOException {
        String request = Streams
                .getString(this
                        .getClass()
                        .getClassLoader()
                        .getResourceAsStream(
                                "sample-soaps/propagerDonneesPointEtAffaire_HeadersKO.xml"));
        SoapMessage requestMessage = validator.newRequestMessage(request);
        List<AssertionError> errors = new ArrayList<AssertionError>();
        if (validator.validateRequest(requestMessage, errors)) {
            fail("Request message should not be valid");
        }
        System.out.println(errors);
    }
}
