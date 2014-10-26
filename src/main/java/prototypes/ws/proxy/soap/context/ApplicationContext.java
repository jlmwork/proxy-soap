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
package prototypes.ws.proxy.soap.context;

import javax.servlet.ServletContext;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;

/**
 *
 * @author jlamande
 */
public class ApplicationContext {

    public static void setProxyConfiguration(ServletContext servletContext, ProxyConfiguration proxyConfig) {
        servletContext.setAttribute(ProxyConfiguration.UID, proxyConfig);
    }

    public static ProxyConfiguration getProxyConfiguration(ServletContext servletContext) {
        return (ProxyConfiguration) servletContext.getAttribute(ProxyConfiguration.UID);
    }

    public static void setSoapExchangeRepository(ServletContext servletContext, SoapExchangeRepository repo) {
        servletContext.setAttribute(SoapExchangeRepository.UID, repo);
    }

    public static SoapExchangeRepository getSoapExchangeRepository(ServletContext servletContext) {
        return (SoapExchangeRepository) servletContext.getAttribute(SoapExchangeRepository.UID);
    }

}
