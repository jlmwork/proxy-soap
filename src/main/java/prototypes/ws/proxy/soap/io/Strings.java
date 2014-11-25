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
package prototypes.ws.proxy.soap.io;

import java.io.ByteArrayInputStream;
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

    public static byte[] compressString(String input) {
        //ByteArrayInputStream bAIS = new ByteArrayInputStream(input.getBytes());
        return Streams.compressString(input);
    }

    public static String uncompressString(byte[] input) {
        ByteArrayInputStream bAIS = new ByteArrayInputStream(input);
        return Streams.getString(bAIS, true);
    }
}
