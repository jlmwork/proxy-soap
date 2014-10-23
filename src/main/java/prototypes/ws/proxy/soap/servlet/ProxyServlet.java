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
package prototypes.ws.proxy.soap.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constantes.ProxyErrorConstantes;
import prototypes.ws.proxy.soap.io.Requests;
import prototypes.ws.proxy.soap.io.Streams;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.monitor.ProxyMonitor;

public class ProxyServlet extends AbstractServlet {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ProxyServlet.class);

    private static final long serialVersionUID = 753782663465493431L;

    // following headers must not go back unchanged to the client
    private static final List<String> RESP_HEADERS_TO_IGNORE = Arrays
            .asList(new String[]{"transfer-encoding", "content-encoding",
                "set-cookie", "x-powered-by"});

    private URL resolveTargetUrl(HttpServletRequest request) {
        String uri = Requests.getTarget(request);
        if (Strings.isNullOrEmpty(uri)) {
            throw new IllegalStateException(
                    ProxyErrorConstantes.TARGET_IS_EMPTY);
        }
        if (!uri.matches("^\\w+://.*")) {
            LOGGER.debug("URI doesnt match URL pattern. So add current request host");
            uri = Requests.getHost(request) + "/" + uri;
        }

        // TODO :
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

    /**
     * Recept all request.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug("doRequest");

        URL targetUrl = null;
        try {
            targetUrl = resolveTargetUrl(request);
        } catch (IllegalStateException e1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    e1.getMessage());
            return;
        }

        byte[] body = Streams.getBytes(request.getInputStream());

        ProxyMonitor proxyMonitor = Requests.getProxyMonitor(request);
        HttpURLConnection httpConn = null;

        try {
            httpConn = (HttpURLConnection) targetUrl.openConnection();
            httpConn.setDoOutput(false);

            this.addRequestHeaders(request, httpConn);
            httpConn.setRequestMethod(request.getMethod());
            httpConn.setDoOutput(true);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Body Content : " + new String(body));
            }

            // Send request
            if (body.length > 0) {
                LOGGER.warn("Body Content is Empty");
                httpConn.getOutputStream().write(body);
            }
            boolean gzipped = "gzip".equals(httpConn.getContentEncoding());

            // Get response. If response is gzipped, uncompress it
            try {
                proxyMonitor.setResponseBody(Streams.getString(
                        httpConn.getInputStream(), gzipped));
            } catch (IOException e) {
                LOGGER.debug("Failed to read target response body", e);
            }

            // Make Proxy Result
            proxyMonitor.setResponseCode(httpConn.getResponseCode());
            proxyMonitor.setResponseMessage(httpConn.getResponseMessage());
            proxyMonitor.setHeaders(httpConn.getHeaderFields());
            proxyMonitor.setContentType(httpConn.getContentType());
            proxyMonitor.setContentEncoding(httpConn.getContentEncoding());
            proxyMonitor.setGzipped(gzipped);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Proxy result : " + proxyMonitor);
            }

        } catch (IOException e) {
            LOGGER.error("Proxy call in ERROR");
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }

        // Specific error code treatment
        switch (proxyMonitor.getResponseCode()) {
            case 0:
                // No response
                LOGGER.debug("ResponseCode =  0 !!!");
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, String
                        .format(ProxyErrorConstantes.EMPTY_RESPONSE,
                                targetUrl.toString()));
                return;
            case 404:
                LOGGER.debug("404 returned");
                response.sendError(
                        HttpServletResponse.SC_BAD_GATEWAY,
                        String.format(ProxyErrorConstantes.NOT_FOUND,
                                targetUrl.toString()));
                return;
        }

        // return response with filtered headers
        addResponseHeaders(response, proxyMonitor, RESP_HEADERS_TO_IGNORE);
        response.setStatus(proxyMonitor.getResponseCode());
        // send service request body
        Streams.putStringAndClose(response.getOutputStream(),
                proxyMonitor.getResponseBody());
    }

    /**
     * Set header from http request to http connection.
     *
     * @param req
     * @param httpConn
     */
    private void addRequestHeaders(HttpServletRequest req,
            HttpURLConnection httpConn) {
        String headerName = null;
        for (Enumeration<String> e = req.getHeaderNames(); e.hasMoreElements(); headerName = e
                .nextElement()) {
            // unactivate gzipped request with remote host
            if ((headerName == null)
                    || headerName.toLowerCase().equals("transfer-encoding")) {
                continue;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Request header [" + headerName + "=" + req.getHeader(headerName) + "]");
            }
            httpConn.setRequestProperty(headerName, req.getHeader(headerName));
        }
        httpConn.setRequestProperty("X-Forwarded-For", req.getRemoteHost());
    }

    /**
     * Let pass through or filter HTTP headers returned by the backend
     *
     * @param resp
     * @param proxyResult
     * @param headersToIgnore allow filtering of headers
     */
    private void addResponseHeaders(HttpServletResponse resp,
            ProxyMonitor proxyResult, List<String> headersToIgnore) {

        if (proxyResult.getHeaders() == null) {
            return;
        }

        for (Map.Entry<String, List<String>> respHeader : proxyResult
                .getHeaders().entrySet()) {
            String headerName = respHeader.getKey();
            List<String> headerValues = respHeader.getValue();
            for (String headerValue : headerValues) {
                if ((headerName != null)
                        && !headersToIgnore.contains(headerName.toLowerCase())) {
                    resp.addHeader(headerName, headerValue);
                }
            }
        }
    }
}
