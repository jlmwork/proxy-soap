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

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.junit.Test;
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 *
 * @author JL06436S
 */
public class SandboxTest {

    @Test
    public void test() throws Exception {/*
         RestAssured.baseURI = "http://" + getLocalHostname() + ":8083";
         RestAssured.basePath = "/proxy-soap";
         RestAssured.urlEncodingEnabled = false;
         List<String> ids
         = given().
         when()
         .get("/exchanges?accept=application/json").
         then()
         .statusCode(200)
         .extract()
         .path("id");
         System.out.println(ids.size());
         Assert.assertEquals(36, ids.size());*/

        //body("$", Matchers.empty());

        Map<String, Object> properties = new HashMap<String, Object>(2);
        //properties.put("eclipselink-oxm-xml", "org/example/binding.json");
        properties.put("eclipselink.media-type", "application/json");

        JAXBContext context = JAXBContext.newInstance(new Class[]{SoapExchange.class}, properties);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("eclipselink.media-type", "application/json");

        SoapExchange soap = new SoapExchange();
        marshaller.marshal(soap, System.out);
    }
}
