package prototypes.ws.proxy.soap.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Strings {

    private static final Logger LOGGER = LoggerFactory.getLogger(Strings.class);

    /**
     * Get first part of string str, where string part is separated by regex.
     *
     * @param str
     * @param regex
     * @return
     */
    public static String first(String str, String regex) {
        return part(str, regex, 0);
    }

    /**
     * Get last part of string str, where string part is separated by regex.
     *
     * @param str
     * @param regex
     * @return
     */
    public static String last(String str, String regex) {
        return part(str, regex, -1);
    }

    /**
     * Get the index part of string str, where string part is separated by
     * regex.
     *
     * @param str
     * @param regex
     * @return
     */
    public static String part(String str, String regex, int index) {
        String[] parts = str.split(regex);

        if (parts.length == 0) {
            return null;
        }

        int part = index;

        if (part < 0) {
            part = parts.length - 1;
        }

        if (parts.length <= part) {
            return null;
        }

        return parts[part];
    }

    public static boolean isNullOrEmpty(String str) {
        return ((str == null) || (str.length() < 1));
    }

    public static String toWellFormatedString(String str) {
        return toWellFormatedString(str, "##");
    }

    public static String toWellFormatedString(String str, String token) {
        return str.replaceAll(token, "\n\t");
    }
}
