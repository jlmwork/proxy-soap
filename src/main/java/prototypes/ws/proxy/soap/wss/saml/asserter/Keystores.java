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

import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jlamande
 */
public class Keystores {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(Keystores.class);

    public static Key getKey(URL keystore, String keyStorePass, String keyAlias, String keyPass) throws Exception {
        Key key = readJKS(keystore, keyStorePass).getKey(keyAlias, keyPass.toCharArray());
        if (key == null) {
            throw new IllegalArgumentException("Key not found in keystore");
        }
        LOGGER.debug("Key : {}", key);
        return key;
    }

    public static KeyStore readJKS(URL keystore, String keyStorePass) throws Exception {
        InputStream ksInputStream = keystore.openStream();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(ksInputStream, keyStorePass.toCharArray());
        if (ks == null) {
            throw new IllegalArgumentException("Incorrect Keystore");
        }
        ksInputStream.close();
        return ks;
    }

}
