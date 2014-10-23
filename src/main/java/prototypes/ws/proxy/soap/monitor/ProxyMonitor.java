/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes.ws.proxy.soap.monitor;

import java.util.List;
import java.util.Map;
import prototypes.ws.proxy.soap.io.Strings;

/**
 *
 * @author julamand
 */
public class ProxyMonitor {

    public static final String UID = "proxy.soap.proxy-result";

    private int responseCode;
    private String responseMessage;
    private String responseBody = "";
    private boolean gzipped = false;
    private Map<String, List<String>> headers;
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

    /**
     * @return the headers
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
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
                + Strings.toWellFormatedString(headers.toString(), ",")
                + "##contentType=" + contentType + "##contentEncoding="
                + contentEncoding + "##responseBody=" + responseBody + "]");
    }

}
