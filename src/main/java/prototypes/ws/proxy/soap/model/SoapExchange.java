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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import prototypes.ws.proxy.soap.time.Dates;
import prototypes.ws.proxy.soap.xml.MapAdapter2;
import prototypes.ws.proxy.soap.xml.XmlStrings;

@XmlRootElement
public class SoapExchange implements Serializable {

    public static final transient String UID = "proxy-soap.exchange";

    private Calendar time = new GregorianCalendar();
    // give the request an unique attribute id
    private final String id = new UUID().toString();
    private String to;
    private String from;
    private String operation;
    private String validatorId;
    private long backEndResponseTime = -1L;
    private long proxyInternalTime = -1L;
    private boolean proxyValidating = false;
    private boolean proxyBlocking = false;

    // request
    private byte[] frontEndRequest;
    private byte[] proxyRequest;
    private Map<String, List<String>> frontEndRequestHeaders;
    private Map<String, List<String>> proxyRequestHeaders;
    private Boolean requestSoapValid;
    private Boolean requestXmlValid;
    private List<String> requestSoapErrors;
    private List<String> responseSoapErrors;
    // response
    private byte[] backEndResponse;
    private int backEndResponseCode;
    private int proxyResponseCode;
    private byte[] proxyResponse;
    private Boolean responseSoapValid;
    private Boolean responseXmlValid;

    private Map<String, List<String>> backEndResponseHeaders;
    private Map<String, List<String>> proxyResponseHeaders;
    private List<String> requestXmlErrors;
    private List<String> responseXmlErrors;
    @XmlJavaTypeAdapter(value = MapAdapter2.class)
    private Map<String, byte[]> capturedFields;

    //
    //private byte[] customFields;
    public BackendExchange createBackendExchange() {
        return new BackendExchange(to, frontEndRequest, frontEndRequestHeaders);
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

    public boolean isProxyValidating() {
        return proxyValidating;
    }

    public void setProxyValidation(boolean proxyValidation) {
        this.proxyValidating = proxyValidation;
    }

    public boolean isProxyBlocking() {
        return proxyBlocking;
    }

    public void setProxyBlocking(boolean proxyBlocking) {
        this.proxyBlocking = proxyBlocking;
    }

    @Override
    public String toString() {
        return "SoapExchange{" + "time=" + time + ", id=" + id + ", uri=" + to
                + ", from=" + from + ", " + "operation=" + operation
                + ", validatorId=" + validatorId + ", backEndResponseTime="
                + backEndResponseTime + ", proxyInternalTime="
                + proxyInternalTime + "\n, frontEndRequest=" + frontEndRequest
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
    public byte[] getFrontEndRequest() {
        return frontEndRequest;
    }

    /**
     * Fomatted xml request.
     *
     * @return
     */
    public byte[] getFrontEndRequestAsXML() {
        return XmlStrings.format(frontEndRequest);
    }

    /**
     * @param request the request to set
     */
    public SoapExchange setFrontEndRequest(byte[] request) {
        this.frontEndRequest = request;
        return this;
    }

    /**
     * @return the response
     */
    public void setBackEndResponse(byte[] response) {
        this.backEndResponse = response;
    }

    /**
     * @return the response
     */
    public byte[] getBackEndResponse() {
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
    public byte[] getBackendResponseAsXML() {
        return XmlStrings.format(backEndResponse);
    }

    /**
     * @param response the response to set
     */
    public SoapExchange setResponse(byte[] response) {
        this.backEndResponse = response;
        return this;
    }

    public byte[] getProxyRequest() {
        return proxyRequest;
    }

    public void setProxyRequest(byte[] proxyRequest) {
        this.proxyRequest = proxyRequest;
    }

    public byte[] getProxyResponse() {
        return proxyResponse;
    }

    public void setProxyResponse(byte[] proxyResponse) {
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
    @XmlElement
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
    @XmlElement
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
    public String getTo() {
        return to;
    }

    /**
     * @param uri the uri to set
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * @return the requestErrors
     */
    @XmlElement
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
    @XmlElement
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

    public void setFrontEndRequestHeaders(
            Map<String, List<String>> requestHeaders) {
        this.frontEndRequestHeaders = new java.util.HashMap(requestHeaders);
    }

    public Map<String, List<String>> getBackEndResponseHeaders() {
        return backEndResponseHeaders;
    }

    public void setBackEndResponseHeaders(
            Map<String, List<String>> responseHeaders) {
        this.backEndResponseHeaders = new java.util.HashMap(responseHeaders);
        //this.backEndResponseHeaders.remove(null); // to remove HTTP message with null key
    }

    public Map<String, List<String>> getProxyRequestHeaders() {
        return proxyRequestHeaders;
    }

    public void setProxyRequestHeaders(
            Map<String, List<String>> proxyRequestHeaders) {
        this.proxyRequestHeaders = new java.util.HashMap(proxyRequestHeaders);
    }

    public Map<String, List<String>> getProxyResponseHeaders() {
        return proxyResponseHeaders;
    }

    public void setProxyResponseHeaders(
            Map<String, List<String>> proxyResponseHeaders) {
        this.proxyResponseHeaders = new java.util.HashMap(proxyResponseHeaders);
        //this.proxyResponseHeaders.remove(null); // to remove HTTP message with null key
    }

    public Map<String, byte[]> getCapturedFields() {
        return capturedFields;
    }

    public void setCapturedFields(Map<String, byte[]> capturedFields) {
        this.capturedFields = capturedFields;
    }

    public void addCapturedField(String key, String capturedField) {
        if (this.capturedFields == null) {
            this.capturedFields = new HashMap<String, byte[]>();
        }
        this.capturedFields.put(key, capturedField.getBytes());
    }

}
