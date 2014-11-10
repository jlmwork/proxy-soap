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
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Before;
import org.junit.Test;
import prototypes.ws.proxy.soap.io.Files;

/**
 * Documentation on RestAssured :
 * https://code.google.com/p/rest-assured/wiki/Usage
 *
 * @author JL06436S
 */
public class ProxyTestsIT {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://" + getLocalHostname() + ":8083";
        RestAssured.basePath = "/proxy-soap";
        RestAssured.urlEncodingEnabled = false;
    }

    @Test
    public void test() {
        RestAssured.expect().statusCode(200).contentType("application/json").when()
                .get("/exchanges?accept=application/json");

        RestAssured.expect().statusCode(502)
                .given()
                .body(Files.read("src/test/resources/samples/messages/requests/operation1-req_OK.xml"))
                .when()
                .post("/p/" + RestAssured.baseURI + RestAssured.basePath + "/sample-resp-longtime.jsp");

        RestAssured.expect().statusCode(200).contentType("application/json").when()
                .get("/exchanges?accept=application/json");

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

}
