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
package prototypes.ws.proxy.soap.repository.jpa.converter;

import javax.persistence.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constants.Messages;
import prototypes.ws.proxy.soap.io.Strings;

/**
 *
 * @author JL06436S
 */
public class CompressionConverter implements AttributeConverter<String, byte[]> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExternalStorageConverter.class);

    @Override
    public byte[] convertToDatabaseColumn(String x) {
        if (!Strings.isNullOrEmpty(x)) {
            LOGGER.debug("Compressing Column");
            LOGGER.trace("Column original size : {}", x.length());
            try {
                byte[] bytes = Strings.compressString(x);
                LOGGER.trace("Compressed number of bytes : {}", bytes.length);
                return bytes;
            } catch (Exception ex) {
                LOGGER.warn(Messages.MSG_ERROR_ON, " Column compression", ex.getMessage());
                LOGGER.debug(Messages.MSG_ERROR_DETAILS, ex);
            }
        }
        return new byte[0];
    }

    @Override
    public String convertToEntityAttribute(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            LOGGER.debug("Uncompress Column");
            try {
                return Strings.uncompressString(bytes);
            } catch (Exception ex) {
                LOGGER.warn(Messages.MSG_ERROR_ON, " Column uncompression", ex.getMessage());
                LOGGER.debug(Messages.MSG_ERROR_DETAILS, ex);
                return new String(bytes);
            }
        }
        return "";
    }

}
