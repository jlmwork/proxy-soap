package prototypes.ws.proxy.soap.validation;

import java.util.List;

import com.eviware.soapui.impl.wsdl.submit.WsdlMessageExchange;
import com.eviware.soapui.model.testsuite.AssertionError;

public interface SoapValidator {

    /**
     *
     * @return time of creation as a timestamp
     */
    public String getId();

    /**
     *
     * @return time of creation as a timestamp
     */
    public String getFrom();

    /**
     *
     * @return time of creation as a timestamp
     */
    public Long getCreationTime();

    /**
     * Validate xml format.
     *
     * @param xml
     * @return
     */
    boolean validateXml(String xml, List<AssertionError> errors);

    /**
     * Validate request message with wsdl definition. On failure, errors list
     * contains errors.
     *
     * @param requestMessage
     * @param errors
     * @return
     */
    boolean validateRequest(WsdlMessageExchange requestMessage,
            List<AssertionError> errors);

    /**
     * Validate response message with wsdl definition.
     *
     * @param requestMessage
     * @return
     */
    boolean validateResponse(WsdlMessageExchange responseMessage);

    /**
     * Validate response message with wsdl definition. On failure, errors list
     * contains errors.
     *
     * @param responseMessage
     * @param errors
     * @return
     */
    boolean validateResponse(WsdlMessageExchange responseMessage,
            List<AssertionError> errors);

    /**
     * Make request message object from xml message.
     *
     * @param message
     * @return
     */
    SoapMessage newRequestMessage(String message);

    /**
     * Make response message object from xml message and request message object.
     *
     * @param message
     * @return
     */
    SoapMessage newResponseMessage(String message, SoapMessage requestMessage);

    Object[] getOperations();

    String getUrl();
}
