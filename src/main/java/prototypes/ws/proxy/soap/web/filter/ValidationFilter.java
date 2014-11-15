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
package prototypes.ws.proxy.soap.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import org.xml.sax.SAXException;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.validation.SoapMessage;
import prototypes.ws.proxy.soap.validation.SoapValidator;
import prototypes.ws.proxy.soap.validation.SoapValidatorFactory;
import prototypes.ws.proxy.soap.web.context.ApplicationContext;
import prototypes.ws.proxy.soap.web.context.RequestContext;
import prototypes.ws.proxy.soap.web.io.CaptureServletResponseWrapper;
import prototypes.ws.proxy.soap.web.io.MultiReadHttpServletRequest;
import prototypes.ws.proxy.soap.web.io.Requests;
import prototypes.ws.proxy.soap.xml.XmlStrings;

/**
 * Servlet Filter implementation class ValidationFilter
 */
public class ValidationFilter extends HttpServletFilter {

    private ProxyConfiguration proxyConfig;

    /**
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        proxyConfig = ApplicationContext.getProxyConfiguration(this.getServletContext());
    }

    /**
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        // only validate POST request if validation is activated
        if (proxyConfig.isValidationActive() && "POST".equals(request.getMethod())) {
            // TODO : avoid to use the SoapExchange object, but better a Validation-like object
            // in order to abstract Validation from SoapExchange system (as in ProxyServlet)
            SoapExchange soapExchange = RequestContext.getSoapExchange(request);
            logger.info("Validation is active.");

            MultiReadHttpServletRequest wrappedRequest = Requests.wrap(request);
            CaptureServletResponseWrapper wrappedResponse = Requests.wrap(response);

            // 1] Request validation
            Boolean requestValid = validateInput(request,
                    soapExchange);
            if (!requestValid && proxyConfig.isInBlockingMode()) {
                logger.info("Proxy is in blocking mode and this request is invalid.");
                Requests.sendErrorClient(wrappedRequest, wrappedResponse,
                        "Request message invalid");
                return;
            }

            // 2] pass the request along the filter chain
            chain.doFilter(wrappedRequest, wrappedResponse);

            // 3] Response validation
            Boolean responseValid = validateOutput(wrappedRequest, wrappedResponse,
                    soapExchange);
            if (responseValid != null && !responseValid && proxyConfig.isInBlockingMode()) {
                logger.info("Proxy is in blocking mode and this response is invalid.");
                // must reinit response wrapper to be able to write another response
                // and forget previous one
                wrappedResponse.reinit();
                Requests.sendErrorServer(wrappedRequest, wrappedResponse,
                        "Response message invalid");
                logger.debug("Invalidation response : {} ", wrappedResponse.getContent());
                return;
            }
        } else {
            // pass the request along the filter chain
            chain.doFilter(request, response);
        }
    }

    /**
     * called before the Filter chain if validation is active
     *
     * @param request
     * @param soapValidator
     * @return
     * @throws IOException
     */
    private boolean validateInput(HttpServletRequest request,
            SoapExchange soapExchange)
            throws IOException {
        String requestBodyContent = new String(soapExchange.getFrontEndRequest());

        // 1] XML Well formed ?
        List<String> errors = XmlStrings.validateXml(requestBodyContent);
        boolean valid = (errors.isEmpty());
        soapExchange.setRequestXmlValid(valid);
        logger.info("Is Request XML valid ? " + errors.isEmpty());
        // get the SoapValidator
        SoapValidator soapValidator = findSoapValidator(request, requestBodyContent);
        request.setAttribute("soapValidator", soapValidator);

        // 2] Soap Valid ?
        if (valid && (soapValidator != null)) {
            soapExchange.setValidatorId(soapValidator.getId());
            SoapMessage message = soapValidator
                    .newRequestMessage(requestBodyContent);
            request.setAttribute("requestMessage", message);
            soapExchange.setOperation(message.getOperation().getBindingOperationName());
            List<String> soapErrors = new ArrayList<String>();
            valid = valid && soapValidator.validateRequest(message, soapErrors);
            logger.info("Is Request SOAP valid ? " + valid);
            soapExchange.setRequestSoapErrors(soapErrors);
            if (logger.isDebugEnabled()) {
                logger.debug(soapErrors.toString());
            }
            soapExchange.setRequestSoapValid(valid);
        } else {
            soapExchange.setRequestXmlErrors(errors);
            logger.debug("XML Errors : " + errors);
        }
        return valid;
    }

    /**
     * called after the Filter chain if validation is active
     *
     * @param response
     * @param out
     * @param soapValidator
     * @throws IOException
     */
    private Boolean validateOutput(HttpServletRequest request,
            CaptureServletResponseWrapper response,
            SoapExchange soapExchange)
            throws IOException {
        Boolean valid = null;
        String responseBodyContent = response.getContent();
        logger.debug("Response body Content to validate : {}", responseBodyContent);

        // validate only responses of code or 500 for SoapFaults
        if (!Strings.isNullOrEmpty(responseBodyContent) && (response.getStatus() == HttpServletResponse.SC_OK
                || response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR)) {
            // 1] XML Well formed ?
            List<String> errors = XmlStrings.validateXml(responseBodyContent);
            valid = (errors.isEmpty());
            soapExchange.setResponseXmlValid(valid);
            logger.info("Is Response XML valid ? " + errors.isEmpty());
            SoapValidator soapValidator = (SoapValidator) request.getAttribute("soapValidator");

            // 2] Soap Valid ?
            if (valid && (soapValidator != null)) {
                List<String> soapErrors
                        = new ArrayList<String>();
                SoapMessage requestMessage = (SoapMessage) request
                        .getAttribute("requestMessage");
                if (requestMessage != null) {
                    SoapMessage message = soapValidator.newResponseMessage(
                            responseBodyContent, requestMessage);
                    valid = valid
                            && soapValidator.validateResponse(message, soapErrors);
                    logger.info("Is Response SOAP valid ? " + valid);
                    soapExchange.setResponseSoapErrors(soapErrors);
                    if (logger.isDebugEnabled()) {
                        logger.debug(soapErrors.toString());
                    }
                    soapExchange.setResponseSoapValid(valid);
                } else {
                    logger.info("Cant validate SOAP Response as Request was SOAP invalid");
                }
            } else {
                soapExchange.setResponseXmlErrors(errors);
            }
        } else if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND) {
            valid = false;
        }
        // dont validate some HTTP codes :
        //  - code 400 : it is returned by proxy specially for tracing bad client requests
        // so proxy dont validate its own response
        return valid;
    }

    private SoapValidator findSoapValidator(HttpServletRequest request, String requestBody) {
        SoapValidator soapValidator = null;
        String wsdlPath = request.getParameter("wsdl");

        // create validator on-the-fly
        if (!Strings.isNullOrEmpty(wsdlPath) && proxyConfig.runInDevMode()) {
            logger.info("Specific Validation activated by parameter wsdl");
            logger.warn("The use of the wsdl parameter for on-the-fly WSDL resolution is not recommended for performance of the proxy");
            logger.debug("WSDL=" + wsdlPath);
            soapValidator = SoapValidatorFactory.getInstance().createSoapValidator(wsdlPath);
        } else {
            if (!Strings.isNullOrEmpty(wsdlPath)) {
                logger.error("The use of the wsdl parameter is not authorized in production mode. The parameter has been ignored.");
            }
            QName qname;
            try {
                qname = SoapMessage.getOperationNameFromBody(requestBody);
                soapValidator = SoapValidatorFactory.getInstance().getValidator(qname);
                if (soapValidator == null) {
                    logger.warn("No Validator found for {}", qname);
                } else {
                    logger.info("Validator found for {}", qname);
                }
            } catch (SAXException ex) {
                logger.warn("XML Error while resolving service operation from XML {} ", ex.getMessage());
            } catch (IOException ex) {
                logger.warn("IO error while resolving service operation from XML {} ", ex.getMessage());
            }
        }
        return soapValidator;
    }

}
