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
package prototypes.ws.proxy.soap.web.converter.json;

import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author JL06436S
 */
public abstract class JsonPConverter<T> extends JsonConverter<T> {

    protected static String stripNull(Object o) {
        if (o == null) {
            return "";
        } else if (o instanceof byte[]) {
            return new String((byte[]) o);
        } else {
            return o.toString();
        }
    }

    protected JsonObject formatJsonMap(Map<String, List<String>> map) {
        JsonObjectBuilder oBuilder = Json.createObjectBuilder();
        if (map != null) {
            for (String key : map.keySet()) {
                if (key == null) {
                    oBuilder.add("-", map.get(key).toString());
                } else {
                    oBuilder.add(key, map.get(key).toString());
                }
            }
        }
        return oBuilder.build();
    }

    protected JsonArray formatJsonList(List<String> list) {
        JsonArrayBuilder aBuilder = Json.createArrayBuilder();
        if (list != null) {
            for (String obj : list) {
                aBuilder.add(obj);
            }
        }
        return aBuilder.build();
    }
}
