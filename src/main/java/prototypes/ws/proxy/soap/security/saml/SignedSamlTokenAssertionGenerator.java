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
package prototypes.ws.proxy.soap.security.saml;

import java.net.URL;
import org.opensaml.SAMLAssertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import prototypes.ws.proxy.soap.security.Keystores;
import prototypes.ws.proxy.soap.commons.xml.XmlStrings;

/**
 *
 * @author JL06436S
 */
public class SignedSamlTokenAssertionGenerator extends SamlTokenAssertionGenerator {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(SignedSamlTokenAssertionGenerator.class);

    // default validity to 1 hour
    private static final Long DEFAULT_VALIDITY_TIME = 3600L;

    private static final String CRYPTO_ALGO
            = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";

    public String generate(String login, Long timeInSeconds, String issuer, URL keystore,
            String keyStorePass, String keyAlias, String keyPass) throws Exception {
        Parameters params = Parameters.builder()
                .withLogin(login)
                .withValidityTime(timeInSeconds)
                .withIssuer(issuer)
                .withKeystore(keystore)
                .withKeystorePass(keyStorePass)
                .withKeyAlias(keyAlias)
                .withKeyPass(keyPass)
                .build();
        return this.generate(params);
    }

    public String generate(String login, String issuer, URL keystore,
            String keyStorePass, String keyAlias, String keyPass) throws Exception {
        Parameters params = Parameters.builder()
                .withLogin(login)
                .withValidityTime(DEFAULT_VALIDITY_TIME)
                .withIssuer(issuer)
                .withKeystore(keystore)
                .withKeystorePass(keyStorePass)
                .withKeyAlias(keyAlias)
                .withKeyPass(keyPass)
                .build();
        return this.generate(params);
    }

    public String generate(Parameters params) throws Exception {
        String assertionString = super.generate(params.getLogin(),
                params.getValidityTime(), params.getIssuer());

        // unmarshall XML and load Document
        Document doc = XmlStrings.loadDocumentFromString(assertionString);
        SAMLAssertion samlAssertion = new SAMLAssertion();
        samlAssertion.fromDOM(doc.getDocumentElement());

        samlAssertion.sign(CRYPTO_ALGO, Keystores.getKey(params.getKeystore(),
                params.getKeystorePass(), params.getKeyAlias(),
                params.getKeyPass()), null);
        String signedAssertion = samlAssertion.toString();
        traceAssertion(samlAssertion);
        return signedAssertion;
    }

    public static class Parameters extends ValidatedBean {

        private static final Long MAX_VALIDITY = 120L;//Long.valueOf(Days.ONE.toStandardSeconds().getSeconds());

        //@NotNull
        //@Size(min = 1)
        String login;

        //@Min(value = 1)
        //@Max(value = MAX_VALIDITY)
        Long validityTime;

        //@NotNull
        //@Size(min = 1)
        String issuer;

        //@NotNull
        URL keystore;

        //@NotNull
        //@Size(min = 1)
        String keystorePass;

        //@NotNull
        //@Size(min = 1)
        String keyAlias;

        //@NotNull
        //@Size(min = 1)
        String keyPass;

        public String getLogin() {
            return login;
        }

        public Long getValidityTime() {
            return validityTime;
        }

        public String getIssuer() {
            return issuer;
        }

        public URL getKeystore() {
            return keystore;
        }

        public String getKeystorePass() {
            return keystorePass;
        }

        public String getKeyAlias() {
            return keyAlias;
        }

        public String getKeyPass() {
            return keyPass;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private final Parameters instance = new Parameters();

            public Builder withLogin(String login) {
                instance.login = login;
                return this;
            }

            public Builder withValidityTime(Long validityTime) {
                if (validityTime != null) {
                    instance.validityTime = validityTime;
                } else {
                    instance.validityTime = DEFAULT_VALIDITY_TIME;
                }
                return this;
            }

            public Builder withValidityTime(String validityTime) {
                if (validityTime != null) {
                    instance.validityTime = Long.valueOf(validityTime);
                } else {
                    instance.validityTime = DEFAULT_VALIDITY_TIME;
                }
                return this;
            }

            public Builder withIssuer(String issuer) {
                instance.issuer = issuer;
                return this;
            }

            public Builder withKeystore(URL keystore) {
                instance.keystore = keystore;
                return this;
            }

            public Builder withKeystorePass(String keystorePass) {
                instance.keystorePass = keystorePass;
                return this;
            }

            public Builder withKeyAlias(String keyAlias) {
                instance.keyAlias = keyAlias;
                return this;
            }

            public Builder withKeyPass(String keyPass) {
                instance.keyPass = keyPass;
                return this;
            }

            public Parameters build() {
                instance.validate();
                return instance;
            }
        }
    }
}
