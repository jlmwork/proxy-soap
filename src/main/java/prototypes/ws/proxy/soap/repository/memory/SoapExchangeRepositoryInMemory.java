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
package prototypes.ws.proxy.soap.repository.memory;

import java.util.LinkedList;
import java.util.List;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.io.SoapExchange;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;

/**
 * Repository implementation based on Memory Storage
 *
 * @author jlamande
 */
public class SoapExchangeRepositoryInMemory extends SoapExchangeRepository {

    private final LinkedList<SoapExchange> exchanges = new LinkedList<SoapExchange>();

    public SoapExchangeRepositoryInMemory(ProxyConfiguration proxyConfig) {
        super(proxyConfig);
    }

    @Override
    public SoapExchange get(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public synchronized List<SoapExchange> list() {
        return exchanges;
    }

    @Override
    public synchronized void save(SoapExchange exchange) {
        if (!ignoreExchange(exchange)) {
            exchanges.addFirst(exchange);
            if (exchanges.size() > proxyConfig.getNbMaxExchanges()) {
                exchanges.removeLast();
            }
        }
    }

    @Override
    public synchronized void removeAll() {
        exchanges.clear();
    }

}
