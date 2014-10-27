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
package prototypes.ws.proxy.soap.wss.saml.asserter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author jlamande
 */
public class SamlTokenAssertionGeneratorTest {

    public SamlTokenAssertionGeneratorTest() {
    }

    /**
     * Test of generate method, of class SamlTokenAssertionGenerator.
     */
    @Test
    public void testGeneratePassing() throws Exception {
        String login = "tester";
        Long time = 1200L;
        String issuer = "http://issuer.org";
        SamlTokenAssertionGenerator generator = new SamlTokenAssertionGenerator();
        String assertion = generator.generate(login, time, issuer);
        System.out.println("Assertion : " + assertion);
        assertNotNull("Returned assertion must not be null", assertion);
        assertNotEquals("Returned assertion must not be empty", "", assertion);
        assertTrue("Returned assertion must be in an XML form", assertion.contains("<"));
        assertTrue("Returned assertion must be an SAML Assertion", assertion.contains("Assertion"));
        assertTrue("Returned assertion must provide an SAML token", assertion.contains("AuthenticationStatement"));
        assertTrue("Returned assertion must provide NotBefore", assertion.contains("NotBefore"));
        assertTrue("Returned assertion must provide NotOnOrAfter", assertion.contains("NotOnOrAfter"));
        // control dates
        //NotBefore="2014-06-04T00:37:36.906Z" NotOnOrAfter="2014-07-08T00:37:36.906Z";
        assertEquals("Duration of assertion is not the one expected.", time, extractAssertionDuration(assertion));
        assertTrue("Returned assertion must provide conditions", assertion.contains("Issuer=\"" + issuer + "\""));
        assertTrue("Returned assertion should contain the provided login", assertion.contains(login));
    }

    public Long extractAssertionDuration(String datesWrapper) throws Exception {
        Pattern pattern = Pattern.compile("NotBefore=\"([^\"]+)\" NotOnOrAfter=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(datesWrapper);
        matcher.find();
        String startDateS = matcher.group(1);
        String stopDateS = matcher.group(2);
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        DateTime dtStart = fmt.parseDateTime(startDateS);
        DateTime dtStop = fmt.parseDateTime(stopDateS);
        Long diffInMillis = dtStop.getMillis() - dtStart.getMillis();
        return diffInMillis / 1000;
    }

}
