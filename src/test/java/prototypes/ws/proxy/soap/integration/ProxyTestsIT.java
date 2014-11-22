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
import java.util.List;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.MediaType;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.io.Files;
import prototypes.ws.proxy.soap.model.Sample;

/**
 * Documentation on RestAssured :
 * https://code.google.com/p/rest-assured/wiki/Usage
 *
 * @author JL06436S
 */
public class ProxyTestsIT {

    private static final Logger logger = LoggerFactory.getLogger(ProxyTestsIT.class);

    private static final String URI_SAMPLES = "/resources/sample";

    private static final String FS_SAMPLES_PATH = "src/test/resources/samples";

    private static final String WSDL_PATH = Files.findFromClasspath("samples/definitions/SampleService.wsdl").replaceAll("^file:[/]+", "/");

    private static int counterRequests = 0;

    @BeforeClass
    public static void init() {
        RestAssured.baseURI = "http://" + System.getProperty("it.host", getLocalHostname()) + ":" + System.getProperty("it.port", "8083");
        logger.info("Test Target URL : ", RestAssured.baseURI);
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

        loadSamples();
    }

    private static void loadSamples() {
        createSampleResponse(500, "operation1-resp-fault_KO");
        createSampleResponse(500, "operation1-resp-fault_OK");
        createSampleResponse(200, "operation1-resp_KO");
        createSampleResponse(200, "operation1-resp_OK");
        createSampleResponse(200, "operation2-resp_KO");
        createSampleResponse(200, "operation2-resp_OK");
        createSampleResponse(500, "standard-resp-fault");
        createSampleResponse(200, "badlyformed");
    }

    private static void createSampleResponse(int returnCode, String name) {
        Sample sample
                = new Sample(returnCode, name,
                        Files.read(FS_SAMPLES_PATH + "/messages/responses/" + name + ".xml")
                );
        given()
                .body(buildJson(sample))
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post(URI_SAMPLES)
                .then().statusCode(201);
    }

    private static String buildJson(Sample sample) {
        JsonObjectBuilder oBuidler = Json.createObjectBuilder();
        oBuidler.add("code", sample.getCode())
                .add("name", sample.getName())
                .add("content", sample.getContent());
        return oBuidler.build().toString();
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
    // Utils
    ///////////////////
    public void check(String requestName, String responseSampleName, int returnCodeExpected, String... contentsToCheck) {
        String content = check(requestName, responseSampleName, returnCodeExpected);
        if (contentsToCheck != null && contentsToCheck.length > 0) {
            for (String contentToCheck : contentsToCheck) {
                Assert.assertTrue("Content " + contentToCheck + " not found", content.contains(contentToCheck));
            }
        }
    }

    public String checkProxyMode(String requestName, String responseSampleName, int returnCodeExpected, String block, String validation) {
        counterRequests++;
        String samplesPath = "/p/" + RestAssured.baseURI + RestAssured.basePath + URI_SAMPLES;

        String content = given()
                .body(Files.read(FS_SAMPLES_PATH + "/messages/requests/" + requestName + ".xml"))
                .when()
                .post(samplesPath + "/" + responseSampleName + "/content")
                .then().statusCode(returnCodeExpected)
                .header("X-Filtering-Blocking", block)
                .header("X-Filtering-Validation", validation)
                .extract().response().body().print();
        return content;
    }

    public String checkWithProxyStatus(String requestName, String responseSampleName, String proxyStatus, int returnCodeExpected) {
        counterRequests++;
        String samplesPath = "/p/" + RestAssured.baseURI + RestAssured.basePath + URI_SAMPLES;

        String content = given()
                .body(Files.read(FS_SAMPLES_PATH + "/messages/requests/" + requestName + ".xml"))
                .when()
                .post(samplesPath + "/" + responseSampleName + "/content")
                .then().statusCode(returnCodeExpected)
                .header("X-Filtered-Status", proxyStatus)
                .extract().response().body().print();
        return content;
    }

    public String check(String requestName, String responseSampleName, int returnCodeExpected) {
        counterRequests++;
        String samplesPath = "/p/" + RestAssured.baseURI + RestAssured.basePath + URI_SAMPLES;

        String content = given()
                .body(Files.read(FS_SAMPLES_PATH + "/messages/requests/" + requestName + ".xml"))
                .when()
                .post(samplesPath + "/" + responseSampleName + "/content")
                .then().statusCode(returnCodeExpected)
                .extract().response().body().print();
        return content;
    }

    public void checkWithCustomUrl(String requestName, String responseSampleName, int returnCodeExpected, String... contentsToCheck) {
        String content = checkWithCustomUrl(requestName, responseSampleName, returnCodeExpected);
        if (contentsToCheck != null && contentsToCheck.length > 0) {
            for (String contentToCheck : contentsToCheck) {
                Assert.assertTrue("Content " + contentToCheck + " not found", content.contains(contentToCheck));
            }
        }
    }

    public String checkWithCustomUrl(String requestName, String url, int returnCodeExpected) {
        counterRequests++;
        String targetUrl = "/p/" + url;

        String content = given()
                .body(Files.read(FS_SAMPLES_PATH + "/messages/requests/" + requestName + ".xml"))
                .when()
                .post(targetUrl)
                .then().statusCode(returnCodeExpected)
                .extract().response().body().print();
        return content;
    }

    ///////////////////
    // TESTS
    ///////////////////
    @Test
    public void testDefaultMode() {
        // not validable request (no configuration of wsdl paths)
        configureProxyDefaultMode();
        check("operation2-req_OK", "operation2-resp_OK", 200);
    }

    @Test
    public void testNonBlockingValidating() {
        // configure proxy mode in non blocking mode
        configureProxyMode("false", "true");
        checkProxyMode("operation1-req_OK", "operation1-resp_OK", 200, "false", "true");
        // ope 1
        checkWithProxyStatus("operation1-req_OK", "operation1-resp_OK", "true true", 200);
        checkWithProxyStatus("operation1-req_OK", "operation1-resp-fault_OK", "true true", 500);
        check("operation1-req_KO", "operation1-resp_OK", 200);
        check("operation1-req_OK", "operation1-resp_KO", 200);
        check("operation1-req_OK", "operation1-resp-fault_KO", 500);
        // ope 2
        check("operation2-req_OK", "operation2-resp_OK", 200);
        check("operation2-req_OK", "operation2-resp_KO", 200);
        check("operation2-req_OK_noheaders", "operation2-resp_KO", 200);
        check("operation2-req_OK_noheaders", "operation2-resp_KO", 200);
        check("operation2-req_KO_body", "operation2-resp_OK", 200);
        check("operation2-req_KO_headers", "operation2-resp_OK", 200);
        check("operation2-req_OK", "standard-resp-fault", 500);

        // special cases :
        // 404
        check("operation1-req_OK", "unknown", 404);
        // badly formed
        check("badlyformed", "operation2-resp_OK", 200);
        check("operation2-req_OK", "badlyformed", 200);
        // read timeout
        checkWithCustomUrl("operation1-req_OK", RestAssured.baseURI + RestAssured.basePath + "/sample-resp-longtime.jsp", 502, "Time out");
        // connect timeout
        // no host target for loopback calls
        checkWithCustomUrl("operation2-req_OK", RestAssured.basePath.substring(1) + URI_SAMPLES + "/operation2-resp_OK/content", 200);
    }

    @Test
    public void testBlockingValidating() {
        // configure proxy mode in blocking mode
        configureProxyMode("true", "true");
        checkProxyMode("operation1-req_OK", "operation1-resp_OK", 200, "true", "true");
        // ope 1
        check("operation1-req_OK", "operation1-resp_OK", 200);
        check("operation1-req_OK", "operation1-resp-fault_OK", 500);
        check("operation1-req_KO", "operation1-resp_OK", 400);
        check("operation1-req_OK", "operation1-resp_KO", 502);
        check("operation1-req_OK", "operation1-resp-fault_KO", 502);
        // ope 2
        check("operation2-req_OK", "operation2-resp_OK", 200);
        check("operation2-req_OK", "operation2-resp_KO", 502);
        check("operation2-req_OK_noheaders", "operation2-resp_OK", 200);
        check("operation2-req_OK_noheaders", "operation2-resp_KO", 502);
        check("operation2-req_KO_body", "operation2-resp_OK", 400);
        check("operation2-req_KO_headers", "operation2-resp_OK", 400);
        check("operation2-req_OK", "standard-resp-fault", 500);

        // special cases :
        // 404
        check("operation1-req_OK", "unknown", 502, "faultcode");
        // badly formed
        check("badlyformed", "operation2-resp_OK", 400);
        check("operation2-req_OK", "badlyformed", 502, "faultcode");
        // read timeout
        checkWithCustomUrl("operation1-req_OK", RestAssured.baseURI + RestAssured.basePath + "/sample-resp-longtime.jsp", 502, "Time out");
        // connect timeout
        // no host target for loopback calls
        checkWithCustomUrl("operation2-req_OK", RestAssured.basePath.substring(1) + URI_SAMPLES + "/operation2-resp_OK/content", 200);
    }

    @Test
    public void otherTests() {
        // check behavior of bad url provided
        checkWithCustomUrl("operation1-req_OK", "http://test:test:test:port/", 400);
        checkWithCustomUrl("operation1-req_OK", "file://local/file", 400);
    }

    @AfterClass
    public static void end() {
        // let some time for last requests to persist
        try {
            Thread.sleep(2000L);
            // POST CONTROLS
        } catch (InterruptedException ex) {
        }

        // check nb run requests
        List<String> ids
                = given().
                when()
                .get("/exchanges?accept=application/json").
                then()
                .statusCode(200)
                .extract()
                .path("id");
        System.out.println(ids.size());
        Assert.assertEquals(counterRequests, ids.size());

        // cleanup exchanges
        //RestAssured.expect().statusCode(204)
        //        .when().delete("/exchanges");
    }
}
