/*
 * Copyright 2014 JL06436S.
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
package prototypes.ws.proxy.soap.commons.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.eclipse.persistence.oxm.annotations.XmlVariableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * Tried to find as much as possible to not depend on MOXy but map
 * transformation is not an easy task with JAXB The better JAXB solution so far
 * (without XmlVariableNode) was based on DOM :
 * http://stackoverflow.com/questions/3941479/jaxb-how-to-marshall-map-into-keyvalue-key#answer-3945494
 * but the name of the "wrapping" element can't be herited from Xml tags (and
 * Json Marshalling can have different behaviors than the XML example)
 *
 * @author JL06436S
 */
public class MapAdapter2 extends XmlAdapter<MapAdapter2.AdaptedMap2, Map<String, byte[]>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapAdapter2.class);

    public static class AdaptedMap2 {

        @XmlVariableNode("key")
        List<AdaptedEntry2> entries = new ArrayList<AdaptedEntry2>();

    }

    public static class AdaptedEntry2 {

        @XmlTransient
        public String key;

        @XmlValue
        public String value;

    }

    @Override
    public AdaptedMap2 marshal(Map<String, byte[]> map) throws Exception {
        LOGGER.debug("marshalling");
        AdaptedMap2 adaptedMap = new AdaptedMap2();
        try {
            LOGGER.debug("Marshall : {}", map);
            if (map != null) {
                for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                    AdaptedEntry2 adaptedEntry = new AdaptedEntry2();
                    if (entry.getKey() != null) {
                        adaptedEntry.key = entry.getKey();
                    } else {
                        adaptedEntry.key = "";
                    }
                    if (map.get(entry.getKey()) != null) {
                        adaptedEntry.value = new String(map.get(entry.getKey()));
                    } else {
                        adaptedEntry.value = null;
                    }
                    adaptedMap.entries.add(adaptedEntry);
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("Error occured : {}", ex);
        }
        return adaptedMap;
    }

    @Override
    public Map<String, byte[]> unmarshal(AdaptedMap2 adaptedMap) throws Exception {
        LOGGER.debug("unmarshalling");
        List<AdaptedEntry2> adaptedEntries = adaptedMap.entries;
        Map<String, byte[]> map = new HashMap<String, byte[]>(adaptedEntries.size());
        for (AdaptedEntry2 adaptedEntry : adaptedEntries) {
            map.put(adaptedEntry.key, adaptedEntry.value.getBytes());
        }
        return map;
    }

}
