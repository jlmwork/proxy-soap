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
package prototypes.ws.proxy.soap.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.io.Streams;
import prototypes.ws.proxy.soap.web.io.Requests;

/**
 *
 * @author julamand
 */
public class BackendExchange {

    public static final String UID = "proxy.soap.backend-exchange";

    private String uri = "";
    private Long starttime = -1L;
    private String requestBody = "";
    private Map<String, List<String>> requestHeaders = new HashMap<String, List<String>>();
    private int responseCode = 0;
    private String responseBody = "";
    private Map<String, List<String>> responseHeaders = new HashMap<String, List<String>>();
    private Long stoptime = -1L;

    public BackendExchange(String uri, String body, Map<String, List<String>> requestHeaders) {
        this.uri = uri;
        this.requestBody = body;
        this.requestHeaders = requestHeaders;
    }

    public BackendExchange(HttpServletRequest request) {
        this.uri = Requests.getTarget(request);
        try {
            this.requestBody = new String(Streams.getBytes(request.getInputStream()));
        } catch (IOException ex) {
            LoggerFactory.getLogger(BackendExchange.class.getName()).warn("Init Backend Failed on reading request input", ex);
        }
        this.requestHeaders = Requests.getRequestHeaders(request);
    }

    public void start() {
        this.starttime = System.currentTimeMillis();
    }

    public Long stop() {
        this.stoptime = System.currentTimeMillis();
        return this.stoptime - this.starttime;
    }

    public Long getResponseTime() {
        return this.stoptime - this.starttime;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, List<String>> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, List<String>> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    @Override
    public String toString() {
        return "BackendExchange{" + "uri=" + uri + ", starttime=" + starttime + ", requestBody=" + requestBody + ", requestHeaders=" + requestHeaders + ", responseCode=" + responseCode + ", responseBody=" + responseBody + ", responseHeaders=" + responseHeaders + ", stoptime=" + stoptime + '}';
    }

}
