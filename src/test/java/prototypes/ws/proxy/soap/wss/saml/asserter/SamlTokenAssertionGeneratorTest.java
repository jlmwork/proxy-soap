/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package prototypes.ws.proxy.soap.wss.saml.asserter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import static org.junit.Assert.*;

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
        assertNotEquals("Returned assertion must not be empty","", assertion);
        assertTrue("Returned assertion must be in an XML form",assertion.contains("<"));
        assertTrue("Returned assertion must be an SAML Assertion",assertion.contains("Assertion"));
        assertTrue("Returned assertion must provide an SAML token",assertion.contains("AuthenticationStatement"));
        assertTrue("Returned assertion must provide NotBefore",assertion.contains("NotBefore"));
        assertTrue("Returned assertion must provide NotOnOrAfter",assertion.contains("NotOnOrAfter"));
        // control dates
        //NotBefore="2014-06-04T00:37:36.906Z" NotOnOrAfter="2014-07-08T00:37:36.906Z";
        assertEquals("Duration of assertion is not the one expected.", time, extractAssertionDuration(assertion));
        assertTrue("Returned assertion must provide conditions",assertion.contains("Issuer=\""+issuer+"\""));
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
        return diffInMillis/1000;
    }
    
}
