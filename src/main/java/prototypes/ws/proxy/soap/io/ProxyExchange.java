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
package prototypes.ws.proxy.soap.io;

import java.util.List;
import java.util.Map;

/**
 *
 * @author julamand
 */
public class ProxyExchange {

    public static final String UID = "proxy.soap.proxy-exchange";

    private int responseCode;
    private String responseMessage;
    private String responseBody = "";
    private boolean gzipped = false;
    private Map<String, List<String>> requestHeaders;
    private Map<String, List<String>> responseHeaders;
    private String contentType;
    private String contentEncoding;

    /**
     * @return the responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode the responseCode to set
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return the responseMessage
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * @param responseMessage the responseMessage to set
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * @return the responseBody
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * @param responseBody the responseBody to set
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * @return the gzipped
     */
    public boolean isGzipped() {
        return gzipped;
    }

    /**
     * @param gzipped the gzipped to set
     */
    public void setGzipped(boolean gzipped) {
        this.gzipped = gzipped;
    }

    public Map<String, List<String>> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, List<String>> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    /**
     * @return the headers
     */
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * @param headers the headers to set
     */
    public void setResponseHeaders(Map<String, List<String>> headers) {
        this.responseHeaders = headers;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the contentEncoding
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * @param contentEncoding the contentEncoding to set
     */
    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    @Override
    public String toString() {
        return Strings.toWellFormatedString("ProxyResult [##responseCode="
                + responseCode + "##responseMessage=" + responseMessage
                + "##gzipped=" + gzipped + "##headers="
                + Strings.toWellFormatedString(responseHeaders.toString(), ",")
                + "##contentType=" + contentType + "##contentEncoding="
                + contentEncoding + "##responseBody=" + responseBody + "]");
    }

}
