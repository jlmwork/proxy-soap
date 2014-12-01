/*
 * Copyright 2014 jlamande.
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
package prototypes.ws.proxy.soap.exchange.repository.jpa.converter;

import com.eaio.uuid.UUID;
import java.io.File;
import javax.persistence.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ApplicationConfig;
import prototypes.ws.proxy.soap.commons.io.Files;
import prototypes.ws.proxy.soap.commons.io.Strings;
import prototypes.ws.proxy.soap.commons.time.Dates;

/**
 *
 * @author jlamande
 */
public class ExternalStorageConverter implements AttributeConverter<byte[], String> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExternalStorageConverter.class);

    @Override
    public String convertToDatabaseColumn(byte[] x) {
        if (x != null && x.length > 0) {
            // dir to store contents
            String storagePath = createStorageDir();
            String id = new UUID().toString();
            String requestStoragePath = storagePath + id + "-content.xml.gz";
            LOGGER.debug("Column stored to : {}", requestStoragePath);
            Files.writeCompressed(requestStoragePath, x);
            return requestStoragePath;
        }
        return "";
    }

    @Override
    public byte[] convertToEntityAttribute(String y) {
        if (!Strings.isNullOrEmpty(y)) {
            String requestStoragePath = y;
            LOGGER.debug("Column loaded from : {}", requestStoragePath);
            return Files.readBytesCompressed(requestStoragePath);
        }
        return new byte[0];
    }

    private String createStorageDir() {
        String dirPath = ApplicationConfig.EXCHANGES_STORAGE_PATH + Dates.getFormattedDate(Dates.YYYYMMDD_HH) + File.separator;
        LOGGER.debug("External Storage files path : {}", dirPath);
        (new File(dirPath)).mkdirs();
        return dirPath;
    }

}
