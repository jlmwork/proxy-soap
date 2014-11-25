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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constantes.ProxyErrorConstantes;
import prototypes.ws.proxy.soap.io.Strings;

public class Requests {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Requests.class);

    public static final String HEADER_AUTH = "Authorization";

    private static final Pattern charsetPattern = Pattern.compile(".*charset=(.*)\\W?$");

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
            LOGGER.debug("Find Target - RequestURI={}", request.getRequestURI());
            LOGGER.debug("Find Target - Extracted={}", sbToExtract.toString());
            LOGGER.debug("Find Target - Target={}", target);
        }
        return target;
    }

    public static URL resolveTargetUrl(HttpServletRequest request, String uri) {
        if (Strings.isNullOrEmpty(uri)) {
            throw new IllegalStateException(
                    ProxyErrorConstantes.TARGET_IS_EMPTY);
        }
        if (!uri.matches("^\\w+://.*")) {
            LOGGER.debug("URI doesnt match URL pattern. So add current request host");
            uri = Requests.getHost(request) + "/" + uri;
        }

        LOGGER.debug("Target uri : {}", uri);
        URL targetUrl;
        try {
            targetUrl = new URL(uri);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format(
                    ProxyErrorConstantes.INVALID_TARGET, uri));
        }
        return targetUrl;
    }

    public static URL resolveTargetUrl(HttpServletRequest request) {
        return resolveTargetUrl(request, Requests.getTarget(request));
    }

    public static String resolveSoapServiceFromURL(String url) {
        String service = "";
        LOGGER.debug("Resolve service in URL : {}", url);
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
        LOGGER.debug("Resolved : '{}'", service);
        return service;
    }

    public static boolean isHttpPath(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return false;
        }
        str = str.toLowerCase();

        return str.startsWith("http:/") || str.startsWith("https:/");
    }

    public static Map<String, List<String>> getRequestHeaders(HttpServletRequest req) {
        return getRequestHeaders(req, new ArrayList<String>());
    }

    public static Map<String, List<String>> getRequestHeaders(HttpServletRequest req, List<String> headersToIgnore) {
        String headerName = null;
        Map<String, List<String>> headersMap = new HashMap<String, List<String>>();
        for (Enumeration<String> e = req.getHeaderNames(); e.hasMoreElements(); headerName = e
                .nextElement()) {
            if (headerName != null
                    && !headersToIgnore.contains(headerName.toLowerCase())) {
                List<String> values = headersMap.get(headerName);
                if (values == null) {
                    values = new ArrayList<String>();
                }
                values.add(req.getHeader(headerName));
                headersMap.put(headerName, values);
            }
        }
        return headersMap;
    }

    public static void setRequestHeaders(HttpURLConnection httpConn, Map<String, List<String>> headersFrom, List<String> headersToForget) {
        for (Map.Entry<String, List<String>> entry : headersFrom.entrySet()) {
            String headerName = entry.getKey();
            if (headerName != null && !headersToForget.contains(headerName.toLowerCase())) {
                for (String headerValue : entry.getValue()) {
                    LOGGER.trace("set Request Header {}, : {}", headerName, headerValue);
                    httpConn.setRequestProperty(headerName, headerValue);
                }
            }
        }
    }

    /**
     * method prefered to response.sendError in order to be able to capture
     * output via wrapped response and know exact process flow.
     *
     * @param request
     * @param response
     * @param message
     * @throws IOException
     * @throws ServletException
     */
    public static void sendErrorClient(HttpServletRequest request, HttpServletResponse response, String message) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        request.setAttribute("javax.servlet.error.message", message);
        request.getRequestDispatcher("/WEB-INF/views/jsp/soap-fault-client.jsp").forward(request, response);
    }

    /**
     * method prefered to response.sendError in order to be able to capture
     * output via wrapped response and know exact process flow.
     *
     * @param request
     * @param response
     * @param message
     * @param returnCode
     * @throws IOException
     * @throws ServletException
     */
    public static void sendErrorServer(HttpServletRequest request, HttpServletResponse response, String message, int returnCode) throws IOException, ServletException {
        response.setStatus(returnCode);
        request.setAttribute("javax.servlet.error.message", message);
        request.getRequestDispatcher("/WEB-INF/views/jsp/soap-fault-server.jsp").forward(request, response);
    }

    /**
     * method prefered to response.sendError in order to be able to capture
     * output via wrapped response and know exact process flow.
     *
     * @param request
     * @param response
     * @param message
     * @throws IOException
     * @throws ServletException
     */
    public static void sendErrorServer(HttpServletRequest request, HttpServletResponse response, String message) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
        request.setAttribute("javax.servlet.error.message", message);
        request.getRequestDispatcher("/WEB-INF/views/jsp/soap-fault-server.jsp").forward(request, response);
    }

    /**
     * method prefered to response.sendError in order to be able to capture
     * output via wrapped response and know exact process flow.
     *
     * @param request
     * @param response
     * @param message
     * @throws IOException
     * @throws ServletException
     */
    public static void sendInternalErrorServer(HttpServletRequest request, HttpServletResponse response, String message) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("javax.servlet.error.message", message);
        request.getRequestDispatcher("/WEB-INF/views/jsp/soap-fault-server.jsp").forward(request, response);
    }

    /**
     * wraps request for allowing to read request body more than once
     *
     * This wrapping may occur in any ServletFilter as the method is idempotent
     *
     * @param request
     * @return
     */
    public static MultiReadHttpServletRequest wrap(HttpServletRequest request) {
        MultiReadHttpServletRequest wrappedRequest;
        if (!(request instanceof MultiReadHttpServletRequest)) {
            wrappedRequest = new MultiReadHttpServletRequest(request);
        } else {
            wrappedRequest = (MultiReadHttpServletRequest) request;
        }
        return wrappedRequest;
    }

    /**
     * wraps response for post processing its content
     *
     * This wrapping may occur in any ServletFilter as the method is idempotent
     *
     * @param response
     * @return
     */
    public static CaptureServletResponseWrapper wrap(HttpServletResponse response) {
        CaptureServletResponseWrapper wrappedResponse;
        if (!(response instanceof CaptureServletResponseWrapper)) {
            // buffer response content
            wrappedResponse = new CaptureServletResponseWrapper(response);
        } else {
            wrappedResponse = (CaptureServletResponseWrapper) response;
        }
        return wrappedResponse;
    }

    public static String getCharset(Map<String, List<String>> map) {
        String charset = null;
        if (map != null) {
            String contentType = null;
            if (map.get("Content-type") != null && !map.get("Content-type").isEmpty()) {
                contentType = map.get("Content-type").get(0);
            }
            if (map.get("Content-Type") != null && !map.get("Content-Type").isEmpty()) {
                contentType = map.get("Content-type").get(0);
            }
            if (map.get("content-type") != null && !map.get("content-type").isEmpty()) {
                contentType = map.get("Content-type").get(0);
            }
            charset = getCharset(contentType);
        }
        return charset;
    }

    public static String getCharset(String contentType) {
        String charset = "UTF-8";
        if (contentType != null) {
            Matcher m = charsetPattern.matcher(contentType);
            if (m.find()) {
                charset = m.group(1);
            }
            return charset;
        }
        return charset;
    }

}
