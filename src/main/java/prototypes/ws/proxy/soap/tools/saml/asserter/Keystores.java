/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools.saml.asserter;

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
        LOGGER.debug("Key : " + key);
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
