/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.saml.asserter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JL06436S
 */
public class CommandLines {

    private static final org.slf4j.Logger LOGGER
            = LoggerFactory.getLogger(CommandLines.class);

    public static void debugArgs(String[] args) {
        LOGGER.debug(Arrays.asList(args).toString());
    }

    /**
     * captures the command line provided by user
     * 
     * 
     * @return 
     */
    public static String captureCommandLineInput() {
        StringBuilder capturedString = new StringBuilder("");
        Scanner in = new Scanner(System.in);
        String line;
        while ((line = in.nextLine()).trim().length() > 0) {
            LOGGER.debug("line");
            capturedString.append(line).append("\n");
        }
        LOGGER.debug("Captured String : " + capturedString);
        return capturedString.toString();
    }
    
    public static URL parseUrlArg(String path) throws MalformedURLException {
        if(path.startsWith(File.separator) ) {
            // absolute path
            return new URL("file://" + path);
        } else if (path.startsWith("."+File.separator) ) {
            // relative path
            String absPath = "file://" + System.getProperty("user.dir") + path.substring(1);
            LOGGER.debug(absPath);
            return new URL(absPath);
        } else {
            return new URL(path);
        }
    }

}
