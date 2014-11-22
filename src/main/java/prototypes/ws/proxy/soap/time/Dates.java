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
package prototypes.ws.proxy.soap.time;

import java.util.Calendar;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author jlamande
 */
public class Dates {

    public static String YYYYMMDD_HHMMSS = "yyyyMMdd-HHmmss";

    public static String YYYYMMDD_HH = "yyyyMMdd-HH";

    public static String getFormattedDate(String pattern) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        DateTime dt = new DateTime();
        return dt.toString(fmt);
    }

    public static String getFormattedDate(Calendar cal, String pattern) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        DateTime dt = new DateTime(cal);
        return dt.toString(fmt);
    }

    public static Calendar parseToCalendar(String cal, String pattern) {
        DateTime dt = DateTime.parse(cal, DateTimeFormat.forPattern(pattern));
        return dt.toGregorianCalendar();
    }
}
