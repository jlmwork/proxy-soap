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
package prototypes.ws.proxy.soap.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jlamande
 */
public class StringsTest {

    @Test
    public void test() {
        Pattern p = Pattern.compile(".*/exchange/([^\\?]+)");
        String[] ins = new String[]{
            "",
            "exchange",
            "/exchange/",
            "/exchang/",
            "exchange/1fd72bf0-626d-11e4-9660-fa35fcf7a2be",
            "/exchange/1fd72bf0-626d-11e4-9660-fa35fcf7a2be",
            "/exchange/1fd72bf0-626d-11e4-9660-fa35fcf7a2be?",
            ".../exchange/1fd72bf0-626d-11e4-9660-fa35fcf7a2be?test=",
            ".../exchange/1fd72bf0-626d-11e4-9660-fa35fcf7a2be?test=?test=",
            "/proxy-soap/exchange/204d29d0-626e-11e4-98a1-fa35fcf7a2be"
        };
        int found = 0;
        for (String in : ins) {
            Matcher m = p.matcher(in);
            if (m.find()) {
                found++;
                System.out.println(in + " => " + m.group(1));
            }
        }
        Assert.assertEquals(5, found);
    }
}
