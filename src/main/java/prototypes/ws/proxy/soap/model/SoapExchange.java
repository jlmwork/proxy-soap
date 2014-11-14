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

import com.eaio.uuid.UUID;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import prototypes.ws.proxy.soap.reflect.Classes;
import prototypes.ws.proxy.soap.time.Dates;
import prototypes.ws.proxy.soap.xml.XmlStrings;

public class SoapExchange {

    public static final transient String UID = "proxy-soap.exchange";

    public static final transient String[] FIELDS = Classes.getAllFieldsName(SoapExchange.class, new String[]{"serial", "_", "UID"});

    private Calendar time = new GregorianCalendar();
    // give the request an unique attribute id
    private final String id = new UUID().toString();
    private String uri;
    private String from;
    private String operation;
    private String validatorId;
    private long backEndResponseTime = -1L;
    private long proxyInternalTime = -1L;

    // request
    private String frontEndRequest;
    private String proxyRequest;
    private Map<String, List<String>> frontEndRequestHeaders;
    private Map<String, List<String>> proxyRequestHeaders;
    private Boolean requestSoapValid;
    private Boolean requestXmlValid;
    private List<String> requestSoapErrors;
    private List<String> responseSoapErrors;
    // response
    private String backEndResponse;
    private int backEndResponseCode;
    private int proxyResponseCode;
    private String proxyResponse;
    private Boolean responseSoapValid;
    private Boolean responseXmlValid;
    private Map<String, List<String>> backEndResponseHeaders;
    private Map<String, List<String>> proxyResponseHeaders;
    private List<String> requestXmlErrors;
    private List<String> responseXmlErrors;

    public BackendExchange createBackendExchange() {
        return new BackendExchange(uri, frontEndRequest, frontEndRequestHeaders);
    }

    public String getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(String validatorId) {
        this.validatorId = validatorId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "SoapExchange{" + "time=" + time + ", id=" + id + ", uri=" + uri + ", from=" + from + ", "
                + "operation=" + operation + ", validatorId=" + validatorId
                + ", backEndResponseTime=" + backEndResponseTime + ", proxyInternalTime=" + proxyInternalTime
                + "\n, frontEndRequest=" + frontEndRequest
                + "\n, frontEndRequestHeaders=" + frontEndRequestHeaders
                + "\n, proxyRequest=" + proxyRequest
                + "\n, proxyRequestHeaders=" + proxyRequestHeaders
                + "\n, proxyResponseHeaders=" + proxyResponseHeaders
                + "\n, proxyResponse=" + proxyResponse
                + "\n, proxyResponseCode=" + proxyResponseCode
                + "\n, backEndResponse=" + backEndResponse
                + "\n, backEndResponseCode=" + backEndResponseCode
                + "\n, backEndResponseHeaders=" + backEndResponseHeaders
                + "\n, responseSoapValid=" + responseSoapValid
                + ", responseXmlValid=" + responseXmlValid
                + ", requestSoapValid=" + requestSoapValid
                + ", requestXmlValid=" + requestXmlValid
                + ", requestSoapErrors=" + requestSoapErrors
                + ", responseSoapErrors=" + responseSoapErrors
                + ", requestXmlErrors=" + requestXmlErrors
                + ", responseXmlErrors=" + responseXmlErrors + '}';
    }

    /**
     * @return the request
     */
    public String getId() {
        return id;
    }

    /**
     * @return the request
     */
    public String getFrontEndRequest() {
        return frontEndRequest;
    }

    /**
     * Fomatted xml request.
     *
     * @return
     */
    public String getFrontEndRequestAsXML() {
        return XmlStrings.format(frontEndRequest);
    }

    /**
     * @param request the request to set
     */
    public SoapExchange setFrontEndRequest(String request) {
        this.frontEndRequest = request;
        return this;
    }

    /**
     * @return the response
     */
    public void setBackEndResponse(String response) {
        this.backEndResponse = response;
    }

    /**
     * @return the response
     */
    public String getBackEndResponse() {
        return backEndResponse;
    }

    /**
     * @return the response
     */
    public int getBackEndResponseCode() {
        return backEndResponseCode;
    }

    /**
     * @return the response
     */
    public void setBackEndResponseCode(int code) {
        this.backEndResponseCode = code;
    }

    /**
     * Fomatted xml response.
     *
     * @return
     */
    public String getBackendResponseAsXML() {
        return XmlStrings.format(backEndResponse);
    }

    /**
     * @param response the response to set
     */
    public SoapExchange setResponse(String response) {
        this.backEndResponse = response;
        return this;
    }

    public String getProxyRequest() {
        return proxyRequest;
    }

    public void setProxyRequest(String proxyRequest) {
        this.proxyRequest = proxyRequest;
    }

    public String getProxyResponse() {
        return proxyResponse;
    }

    public void setProxyResponse(String proxyResponse) {
        this.proxyResponse = proxyResponse;
    }

    /**
     * @return the response
     */
    public int getProxyResponseCode() {
        return proxyResponseCode;
    }

    /**
     * @param code
     * @return the response
     */
    public void setProxyResponseCode(int code) {
        this.proxyResponseCode = code;
    }

    /**
     * @return the requestValid
     */
    public Boolean getRequestValid() {
        return ((requestXmlValid != null) && requestXmlValid)
                && ((requestSoapValid != null) && requestSoapValid);
    }

    public Boolean getRequestSoapValid() {
        return requestSoapValid;
    }

    /**
     * @param requestValid the requestValid to set
     * @return
     */
    public SoapExchange setRequestSoapValid(Boolean requestValid) {
        this.requestSoapValid = requestValid;
        return this;
    }

    /**
     * @return the responseValid
     */
    public Boolean getResponseValid() {
        return ((responseXmlValid != null) && responseXmlValid)
                && ((responseSoapValid != null) && responseSoapValid);
    }

    public Boolean getResponseSoapValid() {
        return responseSoapValid;
    }

    /**
     * @param responseValid the responseValid to set
     */
    public SoapExchange setResponseSoapValid(Boolean responseValid) {
        this.responseSoapValid = responseValid;
        return this;
    }

    /**
     * @return the reponseTime
     */
    public long getBackEndResponseTime() {
        return backEndResponseTime;
    }

    /**
     * @param reponseTime the reponseTime to set
     */
    public SoapExchange setBackEndResponseTime(long reponseTime) {
        this.backEndResponseTime = reponseTime;
        return this;
    }

    public long getProxyInternalTime() {
        return proxyInternalTime;
    }

    public void setProxyInternalTime(long proxyInternalTime) {
        this.proxyInternalTime = proxyInternalTime;
    }

    /**
     * @return the time
     */
    public Calendar getTime() {
        return time;
    }

    public String getDate() {
        return Dates.getFormattedDate(time, Dates.YYYYMMDD_HHMMSS);
    }

    /**
     * @param time the time to set
     */
    public void setTime(Calendar time) {
        this.time = time;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the requestErrors
     */
    public List<String> getRequestErrors() {
        List allErrors = new ArrayList();
        if (requestXmlErrors != null) {
            allErrors.addAll(requestXmlErrors);
        }
        if (requestSoapErrors != null) {
            allErrors.addAll(requestSoapErrors);
        }
        return allErrors;
    }

    /**
     * @return the requestErrors
     */
    public List<String> getRequestXmlErrors() {
        return requestXmlErrors;
    }

    /**
     * @param requestErrors the requestErrors to set
     */
    public void setRequestXmlErrors(List<String> requestErrors) {
        this.requestXmlErrors = requestErrors;
    }

    /**
     * @return the requestErrors
     */
    public List<String> getResponseErrors() {
        List allErrors = new ArrayList();
        if (responseXmlErrors != null) {
            allErrors.addAll(responseXmlErrors);
        }
        if (responseSoapErrors != null) {
            allErrors.addAll(responseSoapErrors);
        }
        return allErrors;
    }

    /**
     * @return the responseErrors
     */
    public List<String> getResponseXmlErrors() {
        return responseXmlErrors;
    }

    /**
     * @param responseErrors the responseErrors to set
     */
    public void setResponseXmlErrors(List<String> responseErrors) {
        this.responseXmlErrors = responseErrors;
    }

    /**
     * @return the requestErrors
     */
    public List<String> getRequestSoapErrors() {
        return requestSoapErrors;
    }

    /**
     * @param requestErrors the requestErrors to set
     */
    public void setRequestSoapErrors(List<String> requestErrors) {
        this.requestSoapErrors = requestErrors;
    }

    /**
     * @return the responseErrors
     */
    public List<String> getResponseSoapErrors() {
        return responseSoapErrors;
    }

    /**
     * @param responseErrors the responseErrors to set
     */
    public void setResponseSoapErrors(List<String> responseErrors) {
        this.responseSoapErrors = responseErrors;
    }

    public Boolean getRequestXmlValid() {
        return requestXmlValid;
    }

    public void setRequestXmlValid(Boolean requestXmlValid) {
        this.requestXmlValid = requestXmlValid;
    }

    public Boolean getResponseXmlValid() {
        return responseXmlValid;
    }

    public void setResponseXmlValid(Boolean responseXmlValid) {
        this.responseXmlValid = responseXmlValid;
    }

    public Map<String, List<String>> getFrontEndRequestHeaders() {
        return frontEndRequestHeaders;
    }

    public void setFrontEndRequestHeaders(Map<String, List<String>> requestHeaders) {
        this.frontEndRequestHeaders = requestHeaders;
    }

    public Map<String, List<String>> getBackendResponseHeaders() {
        return backEndResponseHeaders;
    }

    public void setBackEndResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.backEndResponseHeaders = responseHeaders;
    }

    public Map<String, List<String>> getProxyRequestHeaders() {
        return proxyRequestHeaders;
    }

    public void setProxyRequestHeaders(Map<String, List<String>> proxyRequestHeaders) {
        this.proxyRequestHeaders = proxyRequestHeaders;
    }

    public Map<String, List<String>> getProxyResponseHeaders() {
        return proxyResponseHeaders;
    }

    public void setProxyResponseHeaders(Map<String, List<String>> proxyResponseHeaders) {
        this.proxyResponseHeaders = proxyResponseHeaders;
    }

}
