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
import prototypes.ws.proxy.soap.model.BackendExchange;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.validation.SoapMessage;
import prototypes.ws.proxy.soap.validation.SoapValidator;
import prototypes.ws.proxy.soap.validation.SoapValidatorFactory;
import prototypes.ws.proxy.soap.web.context.ApplicationContext;
import prototypes.ws.proxy.soap.web.context.RequestContext;
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
            SoapExchange soapExchange = RequestContext.getSoapExchange(request);
            logger.info("Validation is active.");

            // 1] Request validation
            boolean requestValid = validateInput(request,
                    soapExchange);
            //TODO : save request headers when request is blocked
            if (!requestValid && proxyConfig.isInBlockingMode()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Request message invalid");
                return;
            }

            // 2] pass the request along the filter chain
            chain.doFilter(request, response);

            // 3] Response validation
            boolean responseValid = validateOutput(request, response,
                    soapExchange);
            if (!responseValid && proxyConfig.isInBlockingMode()) {
                logger.info("Proxy is in blocking mode.");
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
                        "Response message invalid");
                return;
            }
            // send response back to the client
            response.addHeader("X-Filtered-By", "proxy-soap");
            response.addHeader("X-Filtered-ID", soapExchange.getId());
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
        String requestBodyContent = soapExchange.getFrontEndRequest();

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
        soapExchange.setFrontEndRequest(requestBodyContent);
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
    private boolean validateOutput(HttpServletRequest request,
            HttpServletResponse response,
            SoapExchange soapExchange)
            throws IOException {
        boolean valid = false;
        String responseBodyContent = soapExchange.getBackEndResponse();

        BackendExchange proxyExchange = RequestContext.getBackendExchange(request);

        if (proxyExchange != null && proxyExchange.getResponseCode() == HttpServletResponse.SC_OK) {
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
            soapExchange.setBackEndResponseHeaders(proxyExchange.getResponseHeaders());
        }
        soapExchange.setResponse(responseBodyContent);
        return valid;
    }

    private SoapValidator findSoapValidator(HttpServletRequest request, String requestBody) {
        SoapValidator soapValidator = null;
        String wsdlPath = request.getParameter("wsdl");

        // create validator on-the-fly
        if (!Strings.isNullOrEmpty(wsdlPath)) {
            logger.info("Specific Validation activated by parameter wsdl");
            logger.debug("WSDL=" + wsdlPath);
            soapValidator = SoapValidatorFactory.getInstance().createSoapValidator(wsdlPath);
        } else {
            logger.info("Default WSDL Validation activated (as no wsdl parameter has been provided)");
            //String service = Requests.resolveSoapServiceFromRequest(request);
            //soapValidator = SoapValidatorFactory.createSoapValidator(service);
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
