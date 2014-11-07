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
package prototypes.ws.proxy.soap.web.io;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constantes.ProxyErrorConstantes;
import prototypes.ws.proxy.soap.io.Strings;

public class Requests {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Requests.class);

    public static final String HEADER_AUTH = "Authorization";

    /**
     * Get complete host, e.g. <scheme>://<serverName>:<port>
     *
     * @return
     */
    public static String getHost(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":"
                + request.getServerPort();
    }

    public static String getTarget(HttpServletRequest request) {
        StringBuilder sbToExtract = new StringBuilder();
        sbToExtract.append(request.getContextPath())
                .append(request.getServletPath())
                .append("/");
        String target = request.getRequestURI().replaceFirst(sbToExtract.toString(), "");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Find Target - RequestURI=" + request.getRequestURI());
            LOGGER.debug("Find Target - Extracted=" + sbToExtract.toString());
            LOGGER.debug("Find Target - Target=" + target);
        }
        return target;
    }

    public static URL resolveTargetUrl(HttpServletRequest request) {
        String uri = Requests.getTarget(request);
        if (Strings.isNullOrEmpty(uri)) {
            throw new IllegalStateException(
                    ProxyErrorConstantes.TARGET_IS_EMPTY);
        }
        if (!uri.matches("^\\w+://.*")) {
            LOGGER.debug("URI doesnt match URL pattern. So add current request host");
            uri = Requests.getHost(request) + "/" + uri;
        }

        LOGGER.debug("Target uri : " + uri);
        URL targetUrl;
        try {
            targetUrl = new URL(uri);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format(
                    ProxyErrorConstantes.INVALID_TARGET, uri));
        }
        return targetUrl;
    }

    public static String resolveSoapServiceFromURL(String url) {
        String service = "";
        LOGGER.debug("Resolve service in URL : " + url);
        if (!Strings.isNullOrEmpty(url)) {
            int pos = url.lastIndexOf("/");
            if (pos == -1) {
                pos = url.lastIndexOf("\\");
            }
            if ((pos != -1) && ((pos + 1) < url.length())) {
                service = url.substring(pos + 1);
            }
            if (service.toLowerCase().endsWith(".wsdl")) {
                service = service.substring(0, service.length() - 5);
            }
        }
        LOGGER.debug("Resolved : '" + service + "'");
        return service;
    }

    public static boolean isHttpPath(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return false;
        }
        str = str.toLowerCase();

        return str.startsWith("http:/") || str.startsWith("https:/");
    }

}
