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
package prototypes.ws.proxy.soap.repository.jpa;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.persistence.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.io.Strings;

/**
 *
 * @author JL06436S
 */
public class HeadersConverter implements AttributeConverter<Map<String, List<String>>, String> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(HeadersConverter.class);

    @Override
    public String convertToDatabaseColumn(Map<String, List<String>> map) {
        if (map != null) {
            JsonObjectBuilder oBuilder = Json.createObjectBuilder();
            for (String key : map.keySet()) {
                JsonArrayBuilder aBuilder = Json.createArrayBuilder();
                for (String value : map.get(key)) {
                    aBuilder.add(value);
                }
                String compatKey = (key == null) ? "_" : key;
                oBuilder.add(compatKey, aBuilder.build());
            }
            return oBuilder.build().toString();
        } else {
            return "";
        }
    }

    @Override
    public Map<String, List<String>> convertToEntityAttribute(String dbData) {
        Map outMap = new HashMap<String, List<String>>();
        if (!Strings.isNullOrEmpty(dbData)) {
            JsonReader reader = Json.createReader(new StringReader(dbData));
            JsonStructure jsonst = reader.read();

            try {
                Map<String, JsonValue> jsonMap = (Map<String, JsonValue>) jsonst;
                for (String key : jsonMap.keySet()) {
                    List<String> list = new ArrayList<String>();
                    list.addAll((List) jsonMap.get(key));
                    outMap.put(key, list);
                }
            } catch (ClassCastException ex) {
                LOGGER.warn("Bad class found while converting from db : {}", ex);
            } catch (NullPointerException ex) {
                LOGGER.warn("Null found while converting from db : {}", ex);
            }
        }
        return outMap;
    }

}