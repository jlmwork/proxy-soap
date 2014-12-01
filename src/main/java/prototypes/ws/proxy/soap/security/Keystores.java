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
package prototypes.ws.proxy.soap.security;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.commons.messages.Messages;

/**
 *
 * @author jlamande
 */
public class Keystores {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(Keystores.class);

    private Keystores() {
    }

    public static Key getKey(URL keystore, String keyStorePass, String keyAlias, String keyPass) {
        Key key = null;
        try {
            key = readJKS(keystore, keyStorePass).getKey(keyAlias, keyPass.toCharArray());
            if (key == null) {
                throw new IllegalArgumentException("Key not found in keystore");
            }
        } catch (KeyStoreException ex) {
            LOGGER.error(Messages.MSG_ERROR_DETAILS, ex);
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error(Messages.MSG_ERROR_DETAILS, ex);
        } catch (UnrecoverableKeyException ex) {
            LOGGER.error(Messages.MSG_ERROR_DETAILS, ex);
        }
        LOGGER.debug("Key : {}", key);
        return key;
    }

    public static KeyStore readJKS(URL keystore, String keyStorePass) {
        InputStream ksInputStream = null;
        KeyStore ks = null;
        try {
            ksInputStream = keystore.openStream();
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(ksInputStream, keyStorePass.toCharArray());
            if (ks == null) {
                throw new IllegalArgumentException("Incorrect Keystore");
            }
        } catch (CertificateException ex) {
            LOGGER.error(Messages.MSG_ERROR_DETAILS, ex);
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error(Messages.MSG_ERROR_DETAILS, ex);
        } catch (KeyStoreException ex) {
            LOGGER.error(Messages.MSG_ERROR_DETAILS, ex);
        } catch (IOException ex) {
            LOGGER.error(Messages.MSG_ERROR_DETAILS, ex);
        } finally {
            if (ksInputStream != null) {
                try {
                    ksInputStream.close();
                } catch (IOException ex) {
                    LOGGER.error(Messages.MSG_ERROR_DETAILS, ex);
                }
            }
        }
        return ks;
    }

}
