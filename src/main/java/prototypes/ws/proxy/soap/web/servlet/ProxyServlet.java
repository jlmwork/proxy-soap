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
package prototypes.ws.proxy.soap.web.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.constantes.ProxyErrorConstantes;
import prototypes.ws.proxy.soap.io.Streams;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.model.BackendExchange;
import prototypes.ws.proxy.soap.web.context.ApplicationContext;
import prototypes.ws.proxy.soap.web.context.RequestContext;
import prototypes.ws.proxy.soap.web.io.Requests;

public class ProxyServlet extends AbstractServlet {

    private static final long serialVersionUID = 753782663465493431L;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ProxyServlet.class);

    // following headers must not go back unchanged to the client
    private static final List<String> RESP_HEADERS_TO_IGNORE = Arrays
            .asList(new String[]{"transfer-encoding", "content-encoding",
                "set-cookie", "x-powered-by", "Date"});

    // following headers must not go back unchanged to the client
    private static final List<String> REQ_HEADERS_TO_IGNORE = Arrays
            .asList(new String[]{"transfer-encoding", "cookie"});

    private ProxyConfiguration proxyConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        proxyConfig = ApplicationContext.getProxyConfiguration(this.getServletContext());
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
        HttpURLConnection httpConn = null;
        LOGGER.debug("doRequest");
        BackendExchange backendExchange = RequestContext.getBackendExchange(request);
        LOGGER.trace("BackendExchange Hashcode : {}", Integer.toHexString(backendExchange.hashCode()));
        try {
            URL targetUrl = Requests.resolveTargetUrl(request, backendExchange.getUri());
            httpConn = prepareBackendConnection(targetUrl, request, backendExchange.getRequestHeaders());

            // save final state of request headers
            backendExchange.setRequestHeaders(httpConn.getRequestProperties());

            // Send request
            byte[] body = backendExchange.getRequestBody();

            backendExchange.start();
            if (body.length > 0) {
                httpConn.getOutputStream().write(body);
            } else {
                LOGGER.warn("Body Empty");
            }

            boolean gzipped = "gzip".equals(httpConn.getContentEncoding());
            // Get response. If response is gzipped, uncompress it
            try {
                backendExchange.setResponseBody(Streams.getBytes(httpConn.getInputStream(), gzipped));
            } catch (java.net.SocketTimeoutException ex) {
                throw new IOException("Time out");
            } catch (IOException e) {
                LOGGER.warn("Failed to read target response body {}", e);
                backendExchange.setResponseBody(Streams.getBytes(httpConn.getErrorStream(), gzipped));
            } finally {
                backendExchange.stop();
            }

            // Stores infos
            backendExchange.setResponseCode(httpConn.getResponseCode());
            backendExchange.setResponseHeaders(httpConn.getHeaderFields());

            // Specific error code treatment
            switch (backendExchange.getResponseCode()) {
                case 0:
                    // No response
                    LOGGER.debug("ResponseCode =  0 !!!");
                    Requests.sendErrorServer(request, response, String
                            .format(ProxyErrorConstantes.EMPTY_RESPONSE,
                                    targetUrl.toString()));
                    return;
                case 404:
                    LOGGER.debug("404 returned");
                    Requests.sendErrorServer(request, response,
                            String.format(ProxyErrorConstantes.NOT_FOUND,
                                    targetUrl.toString()), 404);
                    return;
            }

            // return response with filtered headers
            List<String> respHeadersToIgnore = new ArrayList<String>(RESP_HEADERS_TO_IGNORE);
            addResponseHeaders(response, backendExchange, respHeadersToIgnore);
            response.setStatus(backendExchange.getResponseCode());

            response.getOutputStream().write(backendExchange.getResponseBody());
        } catch (IllegalStateException e1) {
            // bad url
            Requests.sendErrorClient(request, response,
                    e1.getMessage());
        } catch (ClassCastException ex) {
            // bad url
            Requests.sendErrorClient(request, response,
                    ex.getMessage());
        } catch (IOException e) {
            LOGGER.error("Backend call in ERROR");
            // bad call
            Requests.sendErrorServer(request, response,
                    e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error during proxying : {}", e);
            // protect from all exceptions
            Requests.sendInternalErrorServer(request, response,
                    e.getMessage());
        } finally {
            LOGGER.trace("BackendExchange Hashcode : {}", Integer.toHexString(backendExchange.hashCode()));
            LOGGER.debug("BackendExchange : {}", backendExchange);
            if (httpConn != null) {
                try {
                    httpConn.disconnect();
                } catch (Exception e) {
                    LOGGER.warn("Error on disconnect {}", e);
                }
            }
        }
    }

    private HttpURLConnection prepareBackendConnection(URL targetUrl, HttpServletRequest request, Map<String, List<String>> headers) throws IOException {
        HttpURLConnection httpConn = null;
        httpConn = (HttpURLConnection) targetUrl.openConnection();
        // timeouts
        httpConn.setConnectTimeout(proxyConfig.getConnectTimeout());
        httpConn.setReadTimeout(proxyConfig.getReadTimeout());

        // type of connection
        httpConn.setDoOutput(true);
        LOGGER.debug("Request method : {}", request.getMethod());
        httpConn.setRequestMethod(request.getMethod());

        // Headers
        List<String> originalHeadersToIgnore = new ArrayList<String>(REQ_HEADERS_TO_IGNORE);
        useAuth(request, originalHeadersToIgnore, httpConn);
        Requests.setRequestHeaders(httpConn, headers, originalHeadersToIgnore);
        httpConn.setRequestProperty("X-Forwarded-For", request.getRemoteAddr());

        // some more headers
        String reqContentType = (!Strings.isNullOrEmpty(request.getContentType()))
                ? request.getContentType()
                : (!Strings.isNullOrEmpty(request.getHeader("Content-Type"))
                ? request.getHeader("Content-Type")
                : "text/xml");
        httpConn.setRequestProperty("Content-Type", reqContentType);

        return httpConn;
    }

    private void useAuth(HttpServletRequest request, List<String> originalHeadersToIgnore, HttpURLConnection httpConn) {
        if (!Strings.isNullOrEmpty(request.getParameter("username"))
                && !Strings.isNullOrEmpty(request.getParameter("password"))) {
            LOGGER.info("Use different username/password pari for backend");
            String userpass = request.getParameter("username") + ":" + request.getParameter("password");
            String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
            originalHeadersToIgnore.add(Requests.HEADER_AUTH.toLowerCase());
            httpConn.setRequestProperty(Requests.HEADER_AUTH, basicAuth);
        }
    }

    /**
     * Let pass through or filter HTTP headers returned by the backend
     *
     * @param resp
     * @param proxyResult
     * @param headersToIgnore allow filtering of headers
     */
    private void addResponseHeaders(HttpServletResponse resp,
            BackendExchange proxyResult, List<String> headersToIgnore) {

        if (proxyResult.getResponseHeaders() == null) {
            return;
        }

        for (Map.Entry<String, List<String>> respHeader : proxyResult
                .getResponseHeaders().entrySet()) {
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
