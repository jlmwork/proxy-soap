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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;

/**
 * Memory Storage Repository implementation based on simple internal cache
 *
 * @author jlamande
 */
public class SoapExchangeInMemoryRepository extends SoapExchangeRepository {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SoapExchangeInMemoryRepository.class);

    private final Map<String, SoapExchange> exchangesMap;

    public SoapExchangeInMemoryRepository(ProxyConfiguration proxyConfig) {
        super(proxyConfig);
        int capacity = proxyConfig.getNbMaxExchanges();
        exchangesMap = Collections.synchronizedMap(new FifoMap<String, SoapExchange>(capacity));
    }

    @Override
    public SoapExchange get(String id) {
        return exchangesMap.get(id);
    }

    @Override
    public SoapExchange get(String id, String[] fields) {
        // fields filtering is not available in this implementation
        return exchangesMap.get(id);
    }

    @Override
    public Collection<SoapExchange> listWithoutContent() {
        return list();
    }

    @Override
    public synchronized Collection<SoapExchange> list() {
        return exchangesMap.values();
    }

    @Override
    public synchronized void save(SoapExchange exchange) {
        if (!ignoreExchange(exchange)) {
            /*exchanges.addFirst(exchange);
             if (exchanges.size() > proxyConfig.getNbMaxExchanges()) {
             exchanges.removeLast();
             }*/
            exchangesMap.put(exchange.getId(), exchange);
        }
    }

    @Override
    public synchronized void removeAll() {
        exchangesMap.clear();
    }

    static class FifoMap<K, V> extends LinkedHashMap<K, V> {

        private int capacity;

        public FifoMap() {
            super();
        }

        public FifoMap(int capacity) {
            super();
            this.capacity = capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
            if (LOGGER.isDebugEnabled() && size() > capacity) {
                LOGGER.debug("Max capacity [{}] reached", capacity);
            }
            return size() > capacity;
        }
    }

}
