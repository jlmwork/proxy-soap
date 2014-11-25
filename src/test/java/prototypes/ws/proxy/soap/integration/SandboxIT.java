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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.junit.Test;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.web.converter.json.SoapExchangeJsonPConverter;

/**
 *
 * @author JL06436S
 */
public class SandboxIT {

    @Test
    public void testJson() throws Exception {/*
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
        mappingJsonMoxy();
    }

    public void mappingJsonCustom() {
        SoapExchangeJsonPConverter converter = new SoapExchangeJsonPConverter();
        String json = converter.toJson(new SoapExchange());
        System.out.println("json : " + json);
    }

    public void mappingJsonMoxy() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(2);
        //properties.put("eclipselink-oxm-xml", "org/example/binding.json");
        properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        // oxm json syntax is inspired by http://www.eclipse.org/eclipselink/xsds/eclipselink_oxm_2_4.xsd

        //properties.put("eclipselink-oxm-xml", SoapExchange.class.getPackage().getName().replace(".", "/") + "/soapexchange-summary-binding.json");
        // properties.put("eclipselink.json.include-root", false); // not necessary
        JAXBContext context = JAXBContext.newInstance(new Class[]{SoapExchange.class}, properties);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("eclipselink.media-type", "application/json");
        System.out.println("Start marshalling");
        SoapExchange soap = new SoapExchange();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> list = new ArrayList<String>();
        list.add("element");
        list.add("element2");
        map.put("key", list);
        soap.setBackEndResponseHeaders(map);
        soap.addCapturedField("myField", "content captured");
        marshaller.marshal(soap, System.out);
    }

}
