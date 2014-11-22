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
package prototypes.ws.proxy.soap.xml;

import java.util.Calendar;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import prototypes.ws.proxy.soap.time.Dates;

/**
 *
 * @author jlamande
 */
public class CalendarAdapter extends XmlAdapter<String, Calendar> {

    @Override
    public Calendar unmarshal(String v) throws Exception {
        return Dates.parseToCalendar(v, Dates.YYYYMMDD_HHMMSS);
    }

    @Override
    public String marshal(Calendar v) throws Exception {
        return Dates.getFormattedDate(v, Dates.YYYYMMDD_HHMMSS);
    }
}
