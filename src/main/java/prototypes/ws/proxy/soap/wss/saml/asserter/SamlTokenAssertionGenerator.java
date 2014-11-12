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
     * TODO: only supports SAML 1.1. Use factory pattern and an interface for
     * Generators for more supported versions
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
        // needs to check for validity before returning
        try {
            assertion.checkValidity();
        } catch (org.opensaml.MalformedException e) {
            LOGGER.error("Assertion invalid " + e.getMessage());
            throw new IllegalArgumentException(e);
        }
        traceAssertion(assertion);

        return assertion.toString();
    }

    protected void traceAssertion(SAMLAssertion assertion) throws Exception {
        // Print the assertion to standard output
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Assertion (pretty printed) : {} ", XMLHelper.prettyPrintXML(assertion.toDOM()));
        }
    }

}
