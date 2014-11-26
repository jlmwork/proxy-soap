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
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jlamande
 */
public class ExpressionHelperTest {

    ExpressionHelper expressionHelper;

    @Before
    public void init() {
        expressionHelper = new ExpressionHelper();
    }

    /**
     * Test of parseCaptureExpressions method, of class ProxyConfiguration.
     */
    @Test
    public void testParseCaptureExpressions() {
        List<CaptureExpression> ces = expressionHelper.parseCaptureExpressions("[{\"name\":\"out\",\"objectField\": \"backEndResponse\",\"body\":\"<out>(.*)</out>\"}]");
        Assert.assertNotNull(ces);
        Assert.assertEquals("out", ces.get(0).getName());
        Assert.assertEquals("<out>(.*)</out>", ces.get(0).getBody());

        // bad format
        ces = expressionHelper.parseCaptureExpressions("[{\"name\":\"out\",\"objectField\": \"backEndResponse\",\"regut>\"}]");
        Assert.assertNotNull(ces);
        Assert.assertEquals(0, ces.size());

        // bad format
        ces = expressionHelper.parseCaptureExpressions("[{\"name\":\"out\",\"objectField\": \"backEndResponse\",\"body\":\"<out>.*</out>\"}]");
        Assert.assertNotNull(ces);
        Assert.assertEquals(0, ces.size());
    }

    /**
     * Test of parseExpressions method, of class ProxyConfiguration.
     */
    @Test
    public void testParseExpressions() {
        List<Expression> ces = expressionHelper.parseExpressions("[{\"name\":\"out\",\"body\":\"<out>OK</out>\"}]");
        Assert.assertNotNull(ces);
        Assert.assertEquals("out", ces.get(0).getName());
        Assert.assertEquals("<out>OK</out>", ces.get(0).getBody());

        // bad format
        ces = expressionHelper.parseExpressions("[{\"name\":\"out\",\"body\":*)</out>\"}]");
        Assert.assertNotNull(ces);
        Assert.assertEquals(0, ces.size());
    }

    @Test
    public void testMarshallExpressions() {
        List<Expression> ces = new ArrayList<Expression>();
        ces.add(new CaptureExpression("out", "<out>(.*)</out>"));
        ces.add(new Expression("in", "<in>.*</in>"));
        String json = expressionHelper.marshallExpressions(ces);
        System.out.println(json);
        Assert.assertTrue(json.startsWith("["));
        Assert.assertTrue(json.contains("out"));
        Assert.assertTrue(json.contains("in"));
    }
}
