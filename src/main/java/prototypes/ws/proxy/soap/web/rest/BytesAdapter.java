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

import java.io.ByteArrayInputStream;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import prototypes.ws.proxy.soap.io.Streams;

/**
 *
 * @author JL06436S
 */
public class BytesAdapter extends XmlAdapter<String, byte[]> {

    @Override
    public String marshal(byte[] bytes) throws Exception {
        if (bytes != null && bytes.length > 0) {
            return Streams.getString(new ByteArrayInputStream(bytes));
        }
        return "";
    }

    @Override
    public byte[] unmarshal(String string) throws Exception {
        if (string != null) {
            return string.getBytes();
        }
        return new byte[0];
    }
}
