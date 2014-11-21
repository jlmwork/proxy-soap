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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.junit.Test;
import prototypes.ws.proxy.soap.model.SoapExchange;

public class XmlUtilsTest {

    @Test
    public void test() {
        String str = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
                + "   <soapenv:Header/>\r\n"
                + "   <soapenv:Body>\r\n"
                + "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";
        // bypass first one
        XmlStrings.cleanXmlRequest(str);
        for (int i = 0; i < 1; i++) {
            long start = System.currentTimeMillis();
            XmlStrings.cleanXmlRequest(str);
            System.out
                    .println("Time : " + (System.currentTimeMillis() - start));
            str = new StringBuilder(str).toString();
        }
    }

    @Test
    public void mapMarshalling() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(SoapExchange.class, List.class);

        SoapExchange soapExchange = new SoapExchange();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> list = new ArrayList<String>();
        list.add("hello");
        list.add("hello2");
        map.put("HelloKey", list);
        soapExchange.setBackEndResponseHeaders(map);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        marshaller.marshal(soapExchange, System.out);

        /*
         JAXBContext jc2 = JAXBContext.newInstance();
         Marshaller marshaller2 = jc2.createMarshaller();
         marshaller2.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
         marshaller2.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
         marshaller2.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
         marshaller2.marshal(new String[]{"test", "test2"}, System.out);*/
    }
}
