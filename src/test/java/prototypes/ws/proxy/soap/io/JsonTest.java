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
package prototypes.ws.proxy.soap.io;

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
import org.junit.Test;

/**
 *
 * @author JL06436S
 */
public class JsonTest {

    @Test
    public void test() {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> strings = new ArrayList<String>();
        strings.add("test");
        strings.add("test");
        strings.add("test");
        map.put("list1", strings);
        map.put("list2", strings);
        map.put("list3", strings);
        JsonObjectBuilder oBuilder = Json.createObjectBuilder();
        for (String key : map.keySet()) {
            JsonArrayBuilder aBuilder = Json.createArrayBuilder();
            for (String value : map.get(key)) {
                aBuilder.add(value);
            }
            oBuilder.add(key, aBuilder.build());
        }
        String jsonString = oBuilder.build().toString();
        System.out.println(jsonString);
        JsonReader reader = Json.createReader(new StringReader(jsonString));
        JsonStructure jsonst = reader.read();

        Map outMap = new HashMap<String, List<String>>();
        try {
            Map<String, JsonValue> jsonMap = (Map<String, JsonValue>) jsonst;
            for (String key : jsonMap.keySet()) {
                List<String> list = new ArrayList<String>();
                list.addAll((List) jsonMap.get(key));
                outMap.put(key, list);
            }
        } catch (ClassCastException ex) {

        } catch (NullPointerException ex) {

        }
        System.out.println(outMap);
    }
}
