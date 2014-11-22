@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(value = BytesAdapter.class, type = byte[].class),
    @XmlJavaTypeAdapter(value = MapAdapter.class, type = Map.class),
    @XmlJavaTypeAdapter(value = CalendarAdapter.class, type = Calendar.class)
})
package prototypes.ws.proxy.soap.model;

import java.util.Calendar;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import prototypes.ws.proxy.soap.web.rest.BytesAdapter;
import prototypes.ws.proxy.soap.xml.CalendarAdapter;
import prototypes.ws.proxy.soap.xml.MapAdapter;
