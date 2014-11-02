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
package prototypes.ws.proxy.soap.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.context.ApplicationContext;
import prototypes.ws.proxy.soap.context.RequestContext;
import prototypes.ws.proxy.soap.io.ProxyExchange;
import prototypes.ws.proxy.soap.io.Requests;
import prototypes.ws.proxy.soap.io.SoapExchange;
import prototypes.ws.proxy.soap.io.Streams;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.io.wrapper.BufferedHttpResponseWrapper;
import prototypes.ws.proxy.soap.io.wrapper.MultiReadHttpServletRequest;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;
import prototypes.ws.proxy.soap.validation.SoapMessage;
import prototypes.ws.proxy.soap.validation.SoapValidator;
import prototypes.ws.proxy.soap.validation.SoapValidatorFactory;
import prototypes.ws.proxy.soap.xml.XmlStrings;

/**
 * Servlet Filter implementation class ValidationFilter
 */
public class ValidationFilter extends HttpServletFilter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ValidationFilter.class);

    private ProxyConfiguration proxyConfig;

    private SoapExchangeRepository exchangeRepository;

    /**
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        proxyConfig = ApplicationContext.getProxyConfiguration(this.getServletContext());
        exchangeRepository = ApplicationContext.getSoapExchangeRepository(this.getServletContext());
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
            SoapExchange soapExchange = new SoapExchange();
            soapExchange.setUri(Requests.getTarget(request));
            soapExchange.setFrom(request.getRemoteAddr());
            LOGGER.info("Validation is active.");

            // Prepare Objects
            // wraps request for allowing to read request body more than once
            // This wrapping must occur in the first called ServletFilter
            HttpServletRequest wrappedRequest = new MultiReadHttpServletRequest(request);
            // wraps response for post processing
            OutputStream out = response.getOutputStream();
            HttpServletResponse wrappedResponse = new BufferedHttpResponseWrapper(response);

            // 1] Request validation
            boolean requestValid = validateInput(wrappedRequest,
                    soapExchange);
            //TODO : save request headers when request is blocked
            if (!requestValid && proxyConfig.isInBlockingMode()) {
                wrappedResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Request message invalid");
                exchangeRepository.save(soapExchange);
                return;
            }

            // 2] pass the request along the filter chain
            long start = System.currentTimeMillis();
            chain.doFilter(wrappedRequest, wrappedResponse);
            long stop = System.currentTimeMillis();
            ProxyExchange proxyExchange = RequestContext.getProxyExchange(request);
            soapExchange.setRequestHeaders(proxyExchange.getRequestHeaders());
            soapExchange.setResponseTime(stop - start);

            // 3] Response validation
            boolean responseValid = validateOutput(wrappedRequest, wrappedResponse,
                    soapExchange);
            if (!responseValid && proxyConfig.isInBlockingMode()) {
                LOGGER.info("Proxy is in blocking mode.");
                wrappedResponse.sendError(HttpServletResponse.SC_BAD_GATEWAY,
                        "Response message invalid");
                // store the exchange
                exchangeRepository.save(soapExchange);
                return;
            }
            // store the exchange
            exchangeRepository.save(soapExchange);
            // send response back to the client
            wrappedResponse.addHeader("X-Filtered-By", "proxy-soap");
            out.write(soapExchange.getResponse().getBytes());
            LOGGER.debug("Response written");
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
        String requestBodyContent = new String(Streams.getBytes(request
                .getInputStream()));

        // 1] XML Well formed ?
        List<String> errors = XmlStrings.validateXml(requestBodyContent);
        boolean valid = (errors.isEmpty());
        soapExchange.setRequestXmlValid(valid);
        LOGGER.info("Is Request XML valid ? " + errors.isEmpty());
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
            LOGGER.info("Is Request SOAP valid ? " + valid);
            soapExchange.setRequestSoapErrors(soapErrors);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(soapErrors.toString());
            }
            soapExchange.setRequestSoapValid(valid);
        } else {
            soapExchange.setRequestXmlErrors(errors);
            LOGGER.debug("XML Errors : " + errors);
        }
        soapExchange.setRequest(requestBodyContent);
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
        String responseBodyContent = new String(
                ((BufferedHttpResponseWrapper) response).getBuffer());

        ProxyExchange proxyExchange = RequestContext.getProxyExchange(request);

        if (proxyExchange != null && proxyExchange.getResponseCode() == HttpServletResponse.SC_OK) {
            // 1] XML Well formed ?
            List<String> errors = XmlStrings.validateXml(responseBodyContent);
            valid = (errors.isEmpty());
            soapExchange.setResponseXmlValid(valid);
            LOGGER.info("Is Response XML valid ? " + errors.isEmpty());
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
                    LOGGER.info("Is Response SOAP valid ? " + valid);
                    soapExchange.setResponseSoapErrors(soapErrors);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(soapErrors.toString());
                    }
                    soapExchange.setResponseSoapValid(valid);
                } else {
                    LOGGER.info("Cant validate SOAP Response as Request was SOAP invalid");
                }
            } else {
                soapExchange.setResponseXmlErrors(errors);
            }
            soapExchange.setResponseHeaders(proxyExchange.getResponseHeaders());
        }
        soapExchange.setResponse(responseBodyContent);
        return valid;
    }

    private SoapValidator findSoapValidator(HttpServletRequest request, String requestBody) {
        SoapValidator soapValidator = null;
        String wsdlPath = request.getParameter("wsdl");

        // create validator on-the-fly
        if (!Strings.isNullOrEmpty(wsdlPath)) {
            LOGGER.info("Specific Validation activated by parameter wsdl");
            LOGGER.debug("WSDL=" + wsdlPath);
            soapValidator = SoapValidatorFactory.getInstance().createSoapValidator(wsdlPath);
        } else {
            LOGGER.info("Default WSDL Validation activated (as no wsdl parameter has been provided)");
            //String service = Requests.resolveSoapServiceFromRequest(request);
            //soapValidator = SoapValidatorFactory.createSoapValidator(service);
            QName qname;
            try {
                qname = SoapMessage.getOperationNameFromBody(requestBody);
                soapValidator = SoapValidatorFactory.getInstance().getValidator(qname);
                if (soapValidator == null) {
                    LOGGER.warn("No Validator found for {}", qname);
                } else {
                    LOGGER.info("Validator found for {}", qname);
                }
            } catch (SAXException ex) {
                LOGGER.warn("XML Error while resolving service operation from XML {} ", ex.getMessage());
            } catch (IOException ex) {
                LOGGER.warn("IO error while resolving service operation from XML {} ", ex.getMessage());
            }
        }
        return soapValidator;
    }

}
