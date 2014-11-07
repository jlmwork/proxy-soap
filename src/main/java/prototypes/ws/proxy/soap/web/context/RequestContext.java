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
package prototypes.ws.proxy.soap.web.context;

import javax.servlet.http.HttpServletRequest;
import prototypes.ws.proxy.soap.model.ProxyExchange;

/**
 *
 * @author jlamande
 */
public class RequestContext {

    public static ProxyExchange getProxyExchange(HttpServletRequest request) {
        ProxyExchange proxyMonitor = (ProxyExchange) request.getAttribute(ProxyExchange.UID);
        if (proxyMonitor == null) {
            proxyMonitor = new ProxyExchange();
            request.setAttribute(ProxyExchange.UID, proxyMonitor);
        }
        return proxyMonitor;
    }
}
