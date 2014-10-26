package prototypes.ws.proxy.soap.io;

import com.eaio.uuid.UUID;
import com.eviware.soapui.model.testsuite.AssertionError;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import prototypes.ws.proxy.soap.xml.XmlStrings;

public class SoapExchange {

    public static final String UID = "proxy.soap.requestmonitor";

    private Calendar time = new GregorianCalendar();
    // give the request an unique attribute id
    private final String id = new UUID().toString();
    private String uri;
    private String from;
    private String request;
    private String response;
    private String message;
    private String operation;
    private String validatorId;
    private Boolean requestSoapValid;
    private Boolean requestXmlValid;
    private Boolean responseSoapValid;
    private Boolean responseXmlValid;
    private List<AssertionError> requestSoapErrors;
    private List<AssertionError> responseSoapErrors;
    private List<AssertionError> requestXmlErrors;
    private List<AssertionError> responseXmlErrors;
    private long responseTime;

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
        return time + " " + uri + " " + requestSoapValid + " "
                + responseSoapValid + " " + responseTime;
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
    public String getRequest() {
        return request;
    }

    /**
     * Fomatted xml request.
     *
     * @return
     */
    public String getRequestAsXML() {
        return XmlStrings.format(request);
    }

    /**
     * @param request the request to set
     */
    public SoapExchange setRequest(String request) {
        this.request = request;
        return this;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * Fomatted xml response.
     *
     * @return
     */
    public String getResponseAsXML() {
        return XmlStrings.format(response);
    }

    /**
     * @param response the response to set
     */
    public SoapExchange setResponse(String response) {
        this.response = response;
        return this;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public SoapExchange setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * True if message is empty.
     *
     * @return
     */
    public boolean isEmptyMessage() {
        return (message == null) || (message.length() == 0);
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
    public long getResponseTime() {
        return responseTime;
    }

    /**
     * @param reponseTime the reponseTime to set
     */
    public SoapExchange setResponseTime(long reponseTime) {
        this.responseTime = reponseTime;
        return this;
    }

    /**
     * @return the time
     */
    public Calendar getTime() {
        return time;
    }

    public String getDate() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(time.getTime());
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
    public List<AssertionError> getRequestXmlErrors() {
        return requestXmlErrors;
    }

    /**
     * @param requestErrors the requestErrors to set
     */
    public void setRequestXmlErrors(List<AssertionError> requestErrors) {
        this.requestXmlErrors = requestErrors;
    }

    /**
     * @return the responseErrors
     */
    public List<AssertionError> getResponseXmlErrors() {
        return responseXmlErrors;
    }

    /**
     * @param responseErrors the responseErrors to set
     */
    public void setResponseXmlErrors(List<AssertionError> responseErrors) {
        this.responseXmlErrors = responseErrors;
    }

    /**
     * @return the requestErrors
     */
    public List<AssertionError> getRequestSoapErrors() {
        return requestSoapErrors;
    }

    /**
     * @param requestErrors the requestErrors to set
     */
    public void setRequestSoapErrors(List<AssertionError> requestErrors) {
        this.requestSoapErrors = requestErrors;
    }

    /**
     * @return the responseErrors
     */
    public List<AssertionError> getResponseSoapErrors() {
        return responseSoapErrors;
    }

    /**
     * @param responseErrors the responseErrors to set
     */
    public void setResponseSoapErrors(List<AssertionError> responseErrors) {
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

}
