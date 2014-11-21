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
package prototypes.ws.proxy.soap.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

/**
 *
 * @author JL06436S
 */
public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, List<String>>> {

    public static class AdaptedMap {

        @XmlVariableNode("key")
        List<AdaptedEntry> entries = new ArrayList<AdaptedEntry>();

    }

    public static class AdaptedEntry {

        @XmlTransient
        public String key;

        @XmlValue
        public List<String> value;

    }

    @Override
    public AdaptedMap marshal(Map<String, List<String>> map) throws Exception {
        System.out.println("Marshal Adapter");
        AdaptedMap adaptedMap = new AdaptedMap();
        for (Entry<String, List<String>> entry : map.entrySet()) {
            AdaptedEntry adaptedEntry = new AdaptedEntry();
            adaptedEntry.key = entry.getKey();
            adaptedEntry.value = entry.getValue();
            adaptedMap.entries.add(adaptedEntry);
        }
        return adaptedMap;
    }

    @Override
    public Map<String, List<String>> unmarshal(AdaptedMap adaptedMap) throws Exception {
        System.out.println("UnMarshal Adapter");
        List<AdaptedEntry> adaptedEntries = adaptedMap.entries;
        Map<String, List<String>> map = new HashMap<String, List<String>>(adaptedEntries.size());
        for (AdaptedEntry adaptedEntry : adaptedEntries) {
            map.put(adaptedEntry.key, adaptedEntry.value);
        }
        return map;
    }

}
