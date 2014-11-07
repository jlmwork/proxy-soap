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

import javax.persistence.AttributeConverter;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.io.Strings;

/**
 *
 * @author JL06436S
 */
public class CompressionConverter implements AttributeConverter<String, String> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExternalStorageConverter.class);

    @Override
    public String convertToDatabaseColumn(String x) {
        if (!Strings.isNullOrEmpty(x)) {
            LOGGER.debug("Compressing Column");
            try {
                return new String((new Base64()).encode(Strings.compressString(x)));
            } catch (Exception ex) {
                LOGGER.warn("Error when compressing Column {}", ex.getMessage());
                return x;
            }
        }
        return x;
    }

    @Override
    public String convertToEntityAttribute(String y) {
        if (!Strings.isNullOrEmpty(y)) {
            LOGGER.debug("Uncompress Column");
            try {
                return Strings.uncompressString((byte[]) (new Base64()).decode(y.getBytes()));
            } catch (Exception ex) {
                LOGGER.warn("Error when uncompressing Column {}", ex.getMessage());
                return y;
            }
        }
        return "";
    }

}
