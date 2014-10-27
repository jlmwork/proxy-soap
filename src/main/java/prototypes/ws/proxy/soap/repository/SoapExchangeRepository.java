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
package prototypes.ws.proxy.soap.repository;

import java.util.List;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.io.SoapExchange;

/**
 *
 * @author jlamande
 */
public abstract class SoapExchangeRepository {

    protected ProxyConfiguration proxyConfig;

    public static final String UID = "proxy.soap.exchange.repository";

    public SoapExchangeRepository(ProxyConfiguration proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    public boolean ignoreExchange(SoapExchange exchange) {
        return (proxyConfig.isIgnoreValidRequests() && exchange.getRequestValid() && exchange.getResponseValid());
    }

    public abstract SoapExchange get(String id);

    public abstract List<SoapExchange> list();

    public abstract void save(SoapExchange monitor);

    public abstract void removeAll();

    public void close() {
    }
}