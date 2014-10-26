/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.saml.asserter;

import java.util.Calendar;
import java.util.Date;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAuthenticationStatement;
import org.opensaml.SAMLNameIdentifier;
import org.opensaml.SAMLSubject;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jlamande
 */
public class SamlTokenAssertionGenerator {
    
    private static final org.slf4j.Logger LOGGER
            = LoggerFactory.getLogger(SamlTokenAssertionGenerator.class);

    /**
     * SAML Version hard-coded
     *
     * @TODO: use factory pattern and an interface for Generators for more supported versions
     *
     */
    //private static final String SAML_VERSION = "1.1";
    private static final String strAuthMethod = "urn:oasis:names:tc:SAML:1.1:am:unspecified";

    public String generate(String login, Long timeInSeconds, String issuer) throws Exception {
        Date currentDate = Calendar.getInstance().getTime();
        // Create the assertion
        SAMLAssertion assertion = new SAMLAssertion(issuer, null, null, null, null, null);
        assertion.setNotBefore(currentDate);
        // default to 1 hour
        timeInSeconds = (timeInSeconds == null) ? 3600 : timeInSeconds;
        assertion.setNotOnOrAfter(new Date(currentDate.getTime()
                + timeInSeconds * 1000));
        // Create the subject
        SAMLSubject subject = new SAMLSubject(new SAMLNameIdentifier(login, "", SAMLNameIdentifier.FORMAT_UNSPECIFIED), null, null, null);

        subject.addConfirmationMethod(SAMLSubject.CONF_SENDER_VOUCHES);

        // Create the authentication statement
        Date date = new Date();
        SAMLAuthenticationStatement authStatement = new SAMLAuthenticationStatement(subject, strAuthMethod, date, null, null, null);

        assertion.addStatement(authStatement);
        traceAssertion(assertion);

        return assertion.toString();
    }

    protected void traceAssertion(SAMLAssertion assertion) throws Exception {
        // Print the assertion to standard output
        LOGGER.debug("Assertion (pretty printed) : ");
        LOGGER.debug(XMLHelper.prettyPrintXML(assertion.toDOM()));
    }

}
