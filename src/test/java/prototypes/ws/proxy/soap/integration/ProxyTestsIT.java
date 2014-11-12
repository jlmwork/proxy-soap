/*
 * Copyright 2014 JL06436S.
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
package prototypes.ws.proxy.soap.integration;

import com.jayway.restassured.RestAssured;
import static com.jayway.restassured.RestAssured.given;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import prototypes.ws.proxy.soap.io.Files;
import prototypes.ws.proxy.soap.web.servlet.SamplesServlet;

/**
 * Documentation on RestAssured :
 * https://code.google.com/p/rest-assured/wiki/Usage
 *
 * @author JL06436S
 */
public class ProxyTestsIT {

    private static final String WSDL_PATH = Files.findFromClasspath("samples/definitions/SampleService.wsdl").replaceAll("^file:[/]+", "/");

    @BeforeClass
    public static void init() {
        RestAssured.baseURI = "http://" + getLocalHostname() + ":8083";
        RestAssured.basePath = "/proxy-soap";
        RestAssured.urlEncodingEnabled = false;
        // cleanup exchanges
        RestAssured.expect().statusCode(204)
                .when().delete("/exchanges");
        given().
                when().
                get("/exchanges?accept=application/json").
                then().statusCode(200).
                body("$", Matchers.empty());

        // TODO : init samples
        loadSamples();
    }

    private static void loadSamples() {
        createSampleResponse(500, "operation1-resp-fault_KO");
        createSampleResponse(500, "operation1-resp-fault_OK");
        createSampleResponse(200, "operation1-resp_KO");
        createSampleResponse(200, "operation1-resp_OK");
        createSampleResponse(200, "operation2-resp_KO");
        createSampleResponse(200, "operation2-resp_OK");
    }

    private static void createSampleResponse(int returnCode, String name) {
        SamplesServlet.Sample sample
                = new SamplesServlet.Sample(returnCode, name,
                        Files.read("src/test/resources/samples/messages/responses/" + name + ".xml")
                );
        given()
                .body(sample.toJson())
                .when()
                .post("/samples")
                .then().statusCode(201);
    }

    private void configureProxyDefaultMode() {
        given().
                param("proxy.soap.blockingmode", "false").
                param("proxy.soap.ignore.exchanges.valid", "false").
                param("proxy.soap.maxexchanges", "30").
                param("proxy.soap.validate", "true").
                param("proxy.soap.run.mode", "1").
                param("proxy.soap.wsdls", "").
                when().
                post("/ui/action/config").
                then().statusCode(200);
    }

    private void configureProxyMode(String blocking, String validating) {
        given().
                param("proxy.soap.blockingmode", blocking).
                param("proxy.soap.ignore.exchanges.valid", "false").
                param("proxy.soap.maxexchanges", "30").
                param("proxy.soap.validate", validating).
                param("proxy.soap.run.mode", "1").
                param("proxy.soap.wsdls", WSDL_PATH).
                when().
                post("/ui/action/config").
                then().statusCode(200);
    }

    public static String getLocalHostname() {
        try {
            InetAddress inetAddr = InetAddress.getLocalHost();
            byte[] addr = inetAddr.getAddress();
            // Convert to dot representation
            String ipAddr = "";
            for (int i = 0; i < addr.length; i++) {
                if (i > 0) {
                    ipAddr += ".";
                }
                ipAddr += addr[i] & 0xFF;
            }
            String hostname = inetAddr.getHostName();
            System.out.println("IP Address: " + ipAddr);
            System.out.println("Hostname: " + hostname);
            return hostname;
        } catch (UnknownHostException e) {
            System.out.println("Host not found: " + e.getMessage());
            return "localhost";
        }
    }

    ///////////////////
    // TESTS
    ///////////////////
    public void check(String requestName, String responseSampleName, int returnCodeExpected) {
        String samplesPath = "/p/" + RestAssured.baseURI + RestAssured.basePath + "/sample";

        given()
                .body(Files.read("src/test/resources/samples/messages/requests/" + requestName + ".xml"))
                .when()
                .post(samplesPath + "/" + responseSampleName)
                .then().statusCode(returnCodeExpected);
    }

    @Test
    public void testNonBlockingValidating() {
        // not validable request (no configuration of wsdl paths)
        configureProxyDefaultMode();
        check("operation2-req_OK", "operation2-resp_OK", 200);

        // configure proxy mode
        configureProxyMode("false", "true");
        check("operation1-req_OK", "operation1-resp-fault_KO", 500);
        check("operation1-req_OK", "operation1-resp-fault_OK", 500);
        check("operation1-req_OK", "operation1-resp_OK", 200);
        check("operation1-req_OK", "operation1-resp_KO", 200);
        check("operation2-req_OK", "operation2-resp_OK", 200);
        check("operation2-req_OK", "operation2-resp_KO", 200);
        check("operation1-req_OK", "unknown", 404);
    }

    @Test
    public void testBlockingValidating() {
        // configure proxy mode
        configureProxyMode("true", "true");
        check("operation1-req_OK", "operation1-resp-fault_KO", 502);
        check("operation1-req_OK", "operation1-resp-fault_OK", 500);
        check("operation1-req_OK", "operation1-resp_OK", 200);
        check("operation1-req_OK", "operation1-resp_KO", 502);
        check("operation2-req_OK", "operation2-resp_OK", 200);
        check("operation2-req_OK", "operation2-resp_KO", 502);
        check("operation2-req_OK", "unknown", 502);
    }

    // TODO : check authorization headers
}
