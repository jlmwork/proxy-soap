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
package prototypes.ws.proxy.soap.xml;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import prototypes.ws.proxy.soap.model.SoapExchange;

public class XmlUtilsTest {

    @Test
    public void test() {
        String str = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
                + "   <soapenv:Header/>\r\n"
                + "   <soapenv:Body>  <test>  </test> \r\n"
                + "   </soapenv:Body> \r\n" + "</soapenv:Envelope>";
        // bypass first one
        String tidyXml = XmlStrings.cleanXmlRequest(str);
        //Assert.assertThat(xmlString, Matchers.containsString("<HelloKey>hello#!#hello2</HelloKey>"));
        System.out.println(tidyXml);
        Assert.assertEquals(1, tidyXml.split("\\n").length);
        Assert.assertEquals(1, tidyXml.split("\\r").length);
        Assert.assertEquals(1, tidyXml.split(">\\W+<").length);
    }

    JAXBContext jaxbContext;
    SoapExchange soapExchange;

    @Before
    public void setup() throws Exception {

        // can use direct MOXy JAXBContextFactory
        // org.eclipse.persistence.jaxb.JAXBContextFactory
        //JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[]{SoapExchange.class}, null);
        jaxbContext = JAXBContext.newInstance(SoapExchange.class, List.class);

        soapExchange = new SoapExchange();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> list = new ArrayList<String>();
        list.add("hello");
        list.add("hello2");
        map.put("HelloKey", list);
        soapExchange.setBackEndResponseHeaders(map);
        soapExchange.setResponse("HELLO WORLD".getBytes());
    }

    private String marshallAndGetResultString(Marshaller marshaller) throws Exception {
        ByteArrayOutputStream xmlBAOS = new ByteArrayOutputStream();
        marshaller.marshal(soapExchange, xmlBAOS);
        return new String(xmlBAOS.toByteArray());
    }

    @Test
    public void xmlMarshallMapsAndBytes() throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        String xmlString = marshallAndGetResultString(marshaller);
        System.out.println("XML : " + xmlString);
        Assert.assertThat(xmlString, Matchers.containsString("<HelloKey>hello#!#hello2</HelloKey>"));
        Assert.assertThat(xmlString, Matchers.containsString("<backEndResponse>HELLO WORLD</backEndResponse>"));
    }

    @Test
    public void jsonMarshallMapsAndBytes() throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        String jsonString = marshallAndGetResultString(marshaller);
        System.out.println("JSON : " + jsonString);
        Assert.assertThat(jsonString, Matchers.containsString("backEndResponseHeaders"));
        Assert.assertThat(jsonString, Matchers.containsString("\"HelloKey\" : \"hello#!#hello2\""));
        Assert.assertThat(jsonString, Matchers.containsString("\"backEndResponse\" : \"HELLO WORLD\""));
    }
}
