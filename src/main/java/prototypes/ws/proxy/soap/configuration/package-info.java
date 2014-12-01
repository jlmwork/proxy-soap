@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(value = PatternAdapter.class, type = Pattern.class
    )
})
@XmlAccessorType(XmlAccessType.FIELD)
package prototypes.ws.proxy.soap.configuration;

import java.util.regex.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import prototypes.ws.proxy.soap.commons.xml.PatternAdapter;
