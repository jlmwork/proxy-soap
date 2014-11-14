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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.junit.Test;
import org.mozilla.universalchardet.UniversalDetector;
import prototypes.ws.proxy.soap.io.Streams;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.web.converter.json.SoapExchangeJsonPConverter;
import prototypes.ws.proxy.soap.web.io.Requests;

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
        //mappingJsonMoxy();
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

        properties.put("eclipselink-oxm-xml", SoapExchange.class.getPackage().getName().replace(".", "/") + "/soapexchange-summary-binding.json");
        // properties.put("eclipselink.json.include-root", false); // not necessary

        JAXBContext context = JAXBContext.newInstance(new Class[]{SoapExchange.class}, properties);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("eclipselink.media-type", "application/json");
        System.out.println("Start marshalling");
        SoapExchange soap = new SoapExchange();
        marshaller.marshal(soap, System.out);
    }

    @Test
    public void callSoap() throws Exception {
        URL targetUrl = new URL("http://zed337j3:6101/sgel/services/MesureServiceRead/MesureServiceRead");
        HttpURLConnection httpConn = null;
        httpConn = (HttpURLConnection) targetUrl.openConnection();
        // type of connection
        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("SOAPAction", "");
        httpConn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        String basicAuth = "Basic " + new String(new Base64().encode("sgel-portail:sgel-portail".getBytes()));
        httpConn.setRequestProperty(Requests.HEADER_AUTH, basicAuth);
        httpConn.getOutputStream().write(bodySoap.getBytes());
        String deEncoding = "" + Charset.defaultCharset();
        String locale = "" + Locale.getDefault();
        System.out.println("Default Charset : " + deEncoding);
        System.out.println("Default Locale : " + locale);
        String responseContent = Streams.getString(
                httpConn.getInputStream(), false);

        System.out.println(responseContent);
        //System.out.println(XmlStrings.format(responseContent));
    }

    private String bodySoap = "<?xml version='1.0' encoding='UTF-8'?>\n"
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v2=\"http://nsge.erdf.fr/sgel/mesure/echange/service/v2\">\n"
            + "   <soapenv:Header/>\n"
            + "   <soapenv:Body>\n"
            + "      <v2:recupererHistoriqueMesuresBMPoint>\n"
            + "         <contexte>\n"
            + "            <!--Optional:-->\n"
            + "            <ucuName>?</ucuName>\n"
            + "            <!--Optional:-->\n"
            + "            <tId>éé</tId>\n"
            + "            <systemeId>?</systemeId>\n"
            + "            <!--Optional:-->\n"
            + "            <login>test@erdf.fr</login>\n"
            + "         </contexte>\n"
            + "         <point>50095768285080</point>\n"
            + "         <acteurDemandeurs>ACM_139</acteurDemandeurs>\n"
            + "      </v2:recupererHistoriqueMesuresBMPoint>\n"
            + "   </soapenv:Body>\n"
            + "</soapenv:Envelope>";

    public void findCharset(String body) throws Exception {
        findCharset(new java.io.ByteArrayInputStream(body.getBytes()));
    }

    public String findCharset(InputStream is) throws Exception {

        byte[] buf = new byte[4096];
        // (1)
        UniversalDetector detector = new UniversalDetector(null);

        // (2)
        int nread;
        while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        // (3)
        detector.dataEnd();

        // (4)
        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            System.out.println("Detected encoding = " + encoding);
        } else {
            System.out.println("No encoding detected.");
        }

        // (5)
        detector.reset();
        return encoding;
    }
}
