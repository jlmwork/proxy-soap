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

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constantes.ApplicationConfig;
import prototypes.ws.proxy.soap.io.Files;
import prototypes.ws.proxy.soap.io.SoapExchange;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.time.Dates;

/**
 *
 * @author JL06436S
 */
public class ExternalStorageListener {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExternalStorageListener.class);

    public void marshall(Object o) {
        if (o != null && o instanceof SoapExchange) {
            SoapExchange exchange = (SoapExchange) o;
            LOGGER.debug("Marshall SoapExchange");
            // dir to store contents
            String storagePath = createStorageDir(exchange);

            if (!Strings.isNullOrEmpty(exchange.getRequest())) {
                String requestStoragePath = storagePath + exchange.getId() + "-request.xml";
                LOGGER.trace("Exchange request stored to : {}", requestStoragePath);
                Files.write(requestStoragePath, exchange.getRequest());
                exchange.setRequest(requestStoragePath);
            }
            if (!Strings.isNullOrEmpty(exchange.getResponse())) {
                String responseStoragePath = storagePath + exchange.getId() + "-response.xml";
                LOGGER.trace("Exchange response stored to : {}", responseStoragePath);
                Files.write(responseStoragePath, exchange.getResponse());
                exchange.setResponse(responseStoragePath);
            }
        }
    }

    public void unmarshall(Object o) {
        if (o instanceof SoapExchange) {
            SoapExchange exchange = (SoapExchange) o;

            LOGGER.debug("Unmarshall SoapExchange");
            if (!Strings.isNullOrEmpty(exchange.getRequest())) {
                String requestStoragePath = exchange.getRequest();
                LOGGER.trace("Exchange request loaded from : {}", requestStoragePath);
                exchange.setRequest(Files.read(requestStoragePath));
            }
            if (!Strings.isNullOrEmpty(exchange.getResponse())) {
                String responseStoragePath = exchange.getResponse();
                LOGGER.trace("Exchange response loaded from : {}", responseStoragePath);
                exchange.setResponse(Files.read(responseStoragePath));
            }
        }
    }

    private String createStorageDir(SoapExchange exchange) {
        String dirPath = ApplicationConfig.EXCHANGES_STORAGE_PATH + Dates.getFormattedDate(exchange.getTime(), Dates.YYYYMMDD_HH) + File.separator;
        LOGGER.debug("Requests files path : " + dirPath);
        (new File(dirPath)).mkdirs();
        return dirPath;
    }
}
