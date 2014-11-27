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
package prototypes.ws.proxy.soap.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 *
 * @author jlamande
 */
public class CaptureExpressionTest {

    /**
     * Test of capture method, of class CaptureExpression.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionNameEmpty() {
        CaptureExpression ce = new CaptureExpression("", "(.*)");
    }

    /**
     * Test of capture method, of class CaptureExpression.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionRegexEmpty() {
        CaptureExpression ce = new CaptureExpression("name", "");
    }

    /**
     * Test of capture method, of class CaptureExpression.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionBadRegex() {
        CaptureExpression ce = new CaptureExpression("name", "(((.*)//\\f");
    }

    /**
     * Test of capture method, of class CaptureExpression.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionRegexWithoutCaptureGroup() {
        CaptureExpression ce = new CaptureExpression("name", ".*");
    }

    /**
     * Test of capture method, of class CaptureExpression.
     */
    @Test
    public void testCaptureFromStrings() {
        String content = "my simple content";
        CaptureExpression ce;

        ce = new CaptureExpression("name", "(.*)");
        Assert.assertEquals(content, ce.capture(content));

        ce = new CaptureExpression("name", "\\W([^\\W]*)\\W");
        Assert.assertEquals("simple", ce.capture(content));

        // only first matching group lust be returned
        Assert.assertEquals("simple", ce.capture(content + " and a little more"));

        content = "<out>OK</out>";
        ce = new CaptureExpression("out", "<out>(.*)</out>");
        Assert.assertEquals("OK", ce.capture(content));
    }

    @Test
    public void testCaptureFromObjectUnkonwnField() {
        Exception ex = new Exception("message");
        CaptureExpression ce;

        ce = new CaptureExpression("exMessage", "detailMessageX", "(.*)");
        String captured = ce.capture(ex);
        Assert.assertEquals("", captured);
    }

    @Test
    public void testCaptureFromObjectField() throws IOException {
        String content = "String going into object";

        Exception ex = new Exception(content);
        CaptureExpression ce;

        ce = new CaptureExpression("exMessage", "detailMessage", "(.*)");
        String captured = ce.capture(ex);
        System.out.println("content : " + captured);
        Assert.assertEquals(content, captured);

        // test with bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream(content.length());
        baos.write(content.getBytes(), 0, content.length());
        ce = new CaptureExpression("bytes", "buf", "(.*)");
        captured = ce.capture(baos);
        baos.close();
        System.out.println("content : " + captured);
        Assert.assertEquals(content, captured);

        SoapExchange exchange = new SoapExchange();
        String soapSample = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:oper=\"http://www.example.org/sampleservice/operation2\">\n"
                + "    <soapenv:Header>\n"
                + "        <oper:headOut2>\n"
                + "            <context>OK</context>\n"
                + "        </oper:headOut2>\n"
                + "    </soapenv:Header>\n"
                + "    <soapenv:Body>\n"
                + "        <oper:operation2Response>\n"
                + "            <out>OK</out>\n"
                + "        </oper:operation2Response>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        exchange.setBackEndResponse(soapSample.getBytes());

        ce = new CaptureExpression("out", "backEndResponse", "<out>(.*)</out>");
        captured = ce.capture(exchange);
        System.out.println("exchange out : " + captured);
        Assert.assertEquals("OK", captured);

    }

}
