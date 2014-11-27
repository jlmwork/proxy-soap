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
package prototypes.ws.proxy.soap.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.commons.lang3.StringUtils;
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
 * but the "wrapping" element can't be herited from Xml tags (and Json
 * Marshalling can have different behaviors)
 *
 * @author JL06436S
 */
public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, List<String>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapAdapter.class);

    public static class AdaptedMap {

        @XmlVariableNode("key")
        List<AdaptedEntry> entries = new ArrayList<AdaptedEntry>();

    }

    public static class AdaptedEntry {

        @XmlTransient
        public String key;

        @XmlValue
        public String value;

    }

    @Override
    public AdaptedMap marshal(Map<String, List<String>> map) throws Exception {
        LOGGER.debug("marshalling");
        AdaptedMap adaptedMap = new AdaptedMap();
        try {
            LOGGER.debug("Marshall : {}", map);
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                AdaptedEntry adaptedEntry = new AdaptedEntry();
                if (entry.getKey() != null) {
                    adaptedEntry.key = entry.getKey();
                } else {
                    adaptedEntry.key = "";
                }
                if (map.get(entry.getKey()) != null) {
                    adaptedEntry.value = StringUtils.join(map.get(entry.getKey()), "#!#");
                } else {
                    adaptedEntry.value = null;
                }
                adaptedMap.entries.add(adaptedEntry);
            }
        } catch (Exception ex) {
            LOGGER.warn("Error occured : {}", ex);
        }
        return adaptedMap;
    }

    @Override
    public Map<String, List<String>> unmarshal(AdaptedMap adaptedMap) throws Exception {
        LOGGER.debug("unmarshalling");
        List<AdaptedEntry> adaptedEntries = adaptedMap.entries;
        Map<String, List<String>> map = new HashMap<String, List<String>>(adaptedEntries.size());
        for (AdaptedEntry adaptedEntry : adaptedEntries) {
            map.put(adaptedEntry.key, Arrays.asList(StringUtils.split(adaptedEntry.value, "#!#")));
        }
        return map;
    }

}
