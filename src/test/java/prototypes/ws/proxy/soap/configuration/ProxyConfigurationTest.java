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

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jlamande
 */
public class ProxyConfigurationTest {

    public ProxyConfigurationTest() {
    }

    /**
     * Test of parseCaptureExpressions method, of class ProxyConfiguration.
     */
    @Test
    public void testParseCaptureExpressions() {
        ProxyConfiguration proxyConfig = new ProxyConfiguration();
        List<CaptureExpression> ces = proxyConfig.parseCaptureExpressions("[{\"name\":\"out\",\"objectField\": \"backEndResponse\",\"regex\":\"<out>(.*)</out>\"}]");
        Assert.assertNotNull(ces);
        Assert.assertEquals("out", ces.get(0).getName());
        Assert.assertEquals("<out>(.*)</out>", ces.get(0).getRegex().toString());

        // bad format
        ces = proxyConfig.parseCaptureExpressions("[{\"name\":\"out\",\"objectField\": \"backEndResponse\",\"regut>\"}]");
        Assert.assertNotNull(ces);
        Assert.assertEquals(0, ces.size());

        // bad format
        ces = proxyConfig.parseCaptureExpressions("[{\"name\":\"out\",\"objectField\": \"backEndResponse\",\"regex\":\"<out>.*</out>\"}]");
        Assert.assertNotNull(ces);
        Assert.assertEquals(0, ces.size());
    }

    /**
     * Test of parseExpressions method, of class ProxyConfiguration.
     */
    @Test
    public void testParseExpressions() {
        ProxyConfiguration proxyConfig = new ProxyConfiguration();
        List<Expression> ces = proxyConfig.parseExpressions("[{\"name\":\"out\",\"objectField\": \"backEndResponse\",\"regex\":\"<out>OK</out>\"}]");
        Assert.assertNotNull(ces);
        Assert.assertEquals("out", ces.get(0).getName());
        Assert.assertEquals("<out>OK</out>", ces.get(0).getRegex().toString());

        // bad format
        ces = proxyConfig.parseExpressions("[{\"name\":\"out\",\"objectField\": \"backEndResponse\",\"regex\":*)</out>\"}]");
        Assert.assertNotNull(ces);
        Assert.assertEquals(0, ces.size());
    }

    @Test
    public void testMarshallExpressions() {
        ProxyConfiguration proxyConfig = new ProxyConfiguration();
        List<Expression> ces = new ArrayList<Expression>();
        ces.add(new CaptureExpression("out", "<out>(.*)</out>"));
        ces.add(new Expression("in", "<in>.*</in>"));
        String json = proxyConfig.marshallExpressions(ces);
        System.out.println(json);
        Assert.assertTrue(json.startsWith("["));
        Assert.assertTrue(json.contains("out"));
        Assert.assertTrue(json.contains("in"));
    }
}
