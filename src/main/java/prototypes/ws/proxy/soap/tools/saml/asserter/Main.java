/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.saml.asserter;

import java.net.URL;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JL06436S
 */
public class Main {

    private static final org.slf4j.Logger LOGGER
            = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Main runner = new Main();
        runner.run(args);
    }

    public void run(String[] args) throws Exception {
        LOGGER.debug("Mode CLI");
        CommandLines.debugArgs(args);

        System.out.println("Provide a soap request and terminate by a blank line"
                + " followed by enter "
                + "\nor simply press enter key to ignore :");
        String soapRequest = CommandLines.captureCommandLineInput();//readFromCommandLinePipedInput();

        if (args.length < 5) {
            System.out.println("Usage : java -jar xxxx.jar cli <login> "
                    + "<token_issuer> <jks_file_path> <jks_passphrase> "
                    + "<key_alias> <key_passphrase> "
                    + "[<validity_interval_in_seconds>]");
        } else {
            SignedSamlTokenAssertionGenerator generator
                    = new SignedSamlTokenAssertionGenerator();

            URL url = CommandLines.parseUrlArg(args[2]);
            LOGGER.debug("JKS url : " + url);
            String assertion;
            try {
                SignedSamlTokenAssertionGenerator.Parameters.Builder paramBuilder = SignedSamlTokenAssertionGenerator.Parameters.builder();
                paramBuilder.withLogin(args[0])
                .withIssuer(args[1])
                .withKeystore(url)
                .withKeystorePass(args[3])
                .withKeyAlias(args[4])
                .withKeyPass(args[5]);
                if (args.length > 6) {
                    paramBuilder.withValidityTime(args[6]);
                }
                assertion = generator.generate(paramBuilder.build());
                if (!"".equals(soapRequest)) {
                    // TODO : insert assertion in soap request
                    System.out.println(assertion);
                } else {
                    System.out.println(assertion);
                }
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage());
                LOGGER.debug(e.getMessage(), e);
            }
        }
    }
}
