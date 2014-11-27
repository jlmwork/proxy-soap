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

import org.junit.Assert;
import org.junit.Test;
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 *
 * @author jlamande
 */
public class BooleanExecutableExpressionTest {

    @Test(expected = IllegalArgumentException.class)
    public void testUnparseableScript() {
        BooleanExecutableExpression expression = new BooleanExecutableExpression("express", "<<{}");
        if (expression.getBody() != null) {
            Assert.fail();
        }
    }

    @Test
    public void testExecScriptError() {
        BooleanExecutableExpression e = new BooleanExecutableExpression("express", "body");
        Boolean result = e.execute(new SoapExchange());
        Assert.assertNull(result);
    }

    @Test
    public void testExecScript() {
        BooleanExecutableExpression e = new BooleanExecutableExpression("express", "backEndResponseTime < 0");
        Boolean result = e.execute(new SoapExchange());
        Assert.assertTrue(result);

        e.setBody("frontEndRequest== null");
        result = e.execute(new SoapExchange());
        Assert.assertNotNull(result);
        Assert.assertTrue(result);

        e.setBody("proxyValidating === false");
        result = e.execute(new SoapExchange());
        Assert.assertNotNull(result);
        Assert.assertTrue(result);

        e.setBody("requestValid === true && responseValid == true");
        SoapExchange s = new SoapExchange();
        s.setRequestXmlValid(true);
        s.setRequestSoapValid(true);
        s.setResponseXmlValid(true);
        s.setResponseSoapValid(true);
        result = e.execute(s);
        Assert.assertNotNull(result);
        Assert.assertTrue(result);
    }

}
