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

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import prototypes.ws.proxy.soap.io.Streams;

public class SoapValidatorTest {

    private static SoapValidatorSoapUI validator;

    private static final String schemaPath = "src/test/resources/samples/definitions/SampleService.wsdl";

    @BeforeClass
    public static void staticSetUp() {
        validator = new SoapValidatorSoapUI(schemaPath);
        validator.getOperationsQName();
    }

    private static String loadSample(String path) {
        return Streams.getString(SoapValidatorTest.class
                .getClassLoader()
                .getResourceAsStream(
                        "samples/messages/" + path));
    }

    @Test(expected = SoapException.class)
    public void loadDefinition() {
        new SoapValidatorSoapUI("");
    }

    @Test
    public void validateXml() {
        if (!validator.validateXml(loadSample("requests/operation1-req_OK.xml"), null)) {
            fail("request is not valid xml.");
        }
    }

    @Test
    public void validateSoap() {
        SoapMessage requestMessage = validator.newRequestMessage(loadSample("requests/operation1-req_OK.xml"));
        List<String> errors = new ArrayList<String>();

        if (!validator.validateXml(requestMessage.getRequestContent(), errors)) {
            fail("Request message is not valid xml");
        }

        // Request message validation
        if (!validator.validateRequest(requestMessage, errors)) {
            fail("Request message not valid");
        }

        // Response message validation
        requestMessage = requestMessage.setResponseContent(loadSample("responses/operation1-resp_OK.xml"));
        if (!validator.validateResponse(requestMessage, errors)) {
            System.out.println(errors);
            fail("Response message not valid");
        }
    }

    @Test
    public void validateBadSoapRequest() {
        SoapMessage requestMessage = validator.newRequestMessage(loadSample("requests/operation1-req_KO.xml"));
        List<String> errors = new ArrayList<String>();

        if (!validator.validateXml(requestMessage.getRequestContent(), errors)) {
            fail("Request message is not valid xml");
        }

        // Request message validation
        if (validator.validateRequest(requestMessage, errors)) {
            fail("Request message valid");
        }
    }

    @Test
    public void validateSoapRequestBadHeaders() {
        SoapMessage requestMessage = validator.newRequestMessage(loadSample("requests/operation2-req_KO_headers.xml"));
        List<String> errors = new ArrayList<String>();
        if (!validator.validateXml(requestMessage.getRequestContent(), errors)) {
            fail("Request message is not valid xml");
        }
        // Request message validation
        if (validator.validateRequest(requestMessage, errors)) {
            fail("Request message valid");
        }
    }

    @Test
    public void validateBadSoapResponse() {
        SoapMessage requestMessage = validator.newRequestMessage(loadSample("requests/operation1-req_OK.xml"));
        List<String> errors = new ArrayList<String>();
        if (!validator.validateXml(requestMessage.getRequestContent(), errors)) {
            fail("Request message is not valid xml");
        }
        // Request message validation
        if (!validator.validateRequest(requestMessage, errors)) {
            fail("Request message not valid");
        }
        // Response message validation
        requestMessage = requestMessage.setResponseContent(loadSample("responses/operation1-resp_KO.xml"));
        if (validator.validateResponse(requestMessage, errors)) {
            fail("Response message valid");
        }
    }

    @Test
    public void validateSoapFault() {
        SoapMessage requestMessage = validator.newRequestMessage(loadSample("requests/operation1-req_OK.xml"));
        List<String> errors = new ArrayList<String>();
        // Response message validation
        requestMessage = requestMessage.setResponseContent(loadSample("responses/operation1-resp-fault_OK.xml"));
        if (!validator.validateResponse(requestMessage, errors)) {
            System.out.println(errors);
            fail("Response message not valid");
        }
    }

    @Test
    public void validateBadSoapFault() {
        SoapMessage requestMessage = validator.newRequestMessage(loadSample("requests/operation1-req_OK.xml"));
        List<String> errors = new ArrayList<String>();
        // Response message validation
        requestMessage = requestMessage.setResponseContent(loadSample("responses/operation1-resp-fault_KO.xml"));
        if (validator.validateResponse(requestMessage, errors)) {
            fail("Fault message valid");
        }
    }

}
