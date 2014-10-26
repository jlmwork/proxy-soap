/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package prototypes.ws.proxy.soap.saml;

import java.net.URL;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JL06436S
 */
@Ignore
public class TestIT {

    private static final org.slf4j.Logger LOGGER
            = LoggerFactory.getLogger(Main.class);
    
    @Test
    public void run() throws Exception {
        URL url = TestIT.class.getClassLoader().getResource("datapower.jks");
        System.out.println("JKS URL : " + url);
        String[] args2 = new String[] {"logintest", "issuer", 
            url.toString(),
            "keypass", "datapower", "keypass"};

        Main runner = new Main();
        runner.run(args2);
    }
}
