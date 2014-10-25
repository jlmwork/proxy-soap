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
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.xmlbeans.XmlError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.io.Requests;
import prototypes.ws.proxy.soap.io.Streams;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.io.wrapper.BufferedHttpResponseWrapper;
import prototypes.ws.proxy.soap.io.wrapper.MultiReadHttpServletRequest;
import prototypes.ws.proxy.soap.monitor.ProxyMonitor;
import prototypes.ws.proxy.soap.monitor.SoapRequestMonitor;
import prototypes.ws.proxy.soap.validation.ExtendedWsdlValidator;
import prototypes.ws.proxy.soap.validation.SoapMessage;
import prototypes.ws.proxy.soap.validation.SoapValidator;
import prototypes.ws.proxy.soap.validation.SoapValidatorFactory;
import prototypes.ws.proxy.soap.xml.XmlUtils;

/**
 * Servlet Filter implementation class ValidationFilter
 */
public class ValidationFilter extends HttpServletFilter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ValidationFilter.class);

    private ProxyConfiguration proxy;

    /**
     * @see Filter#init(FilterConfig)
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        proxy = Requests.getProxy(config.getServletContext());
    }

    /**
     *
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     *
     */
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (proxy.isValidationActive() && "POST".equals(request.getMethod())) {
            SoapRequestMonitor monitor = Requests.getRequestMonitor(
                    this.config.getServletContext(), request);
            monitor.setUri(Requests.getTarget(request));
            monitor.setFrom(request.getRemoteAddr());
            LOGGER.info("Validation is active.");

            // Prepare Objects
            // wraps request for allowing to read request body more than once
            // This wrapping must occur in the first called ServletFilter
            request = new MultiReadHttpServletRequest(request);
            // wraps response for post processing
            response = new BufferedHttpResponseWrapper(response);
            OutputStream out = response.getOutputStream();

            // creates a SoapValidator
            SoapValidator validator = getSoapValidator(request);

            // 1] Request validation
            boolean requestValid = validateInput(request, validator,
                    monitor);
            if (!requestValid && proxy.isInBlockingMode()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Request message invalid");
                return;
            }

            // 2] pass the request along the filter chain
            long start = System.currentTimeMillis();
            chain.doFilter(request, response);
            long stop = System.currentTimeMillis();
            monitor.setResponseTime(stop - start);

            // 3] Response validation
            boolean responseValid = validateOutput(request, response,
                    out, validator, monitor);
            if (!responseValid && proxy.isInBlockingMode()) {
                LOGGER.info("Proxy is in blocking mode.");
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
                        "Response message invalid");
                return;
            }
            if (requestValid && responseValid && proxy.isIgnoreValidRequests()) {
                Requests.getMonitorManager(this.config.getServletContext()).getRequests().remove(monitor);
            }
            // send response back to the client
            response.addHeader("X-Filtered-By", "proxy-soap");
            out.write(monitor.getResponse().getBytes());
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
            SoapValidator soapValidator, SoapRequestMonitor monitor)
            throws IOException {
        String requestBodyContent = new String(Streams.getBytes(request
                .getInputStream()));

        // 1] XML Well formed ?
        List<XmlError> errors = XmlUtils.validateXml(requestBodyContent);
        boolean valid = (errors.isEmpty());
        monitor.setRequestXmlValid(valid);
        LOGGER.info("Is Request XML valid ? " + errors.isEmpty());

        // 2] Soap Valid ?
        if (valid && (soapValidator != null)) {
            monitor.setValidatorId(soapValidator.getId());
            SoapMessage message = soapValidator
                    .newRequestMessage(requestBodyContent);
            request.setAttribute("requestMessage", message);
            monitor.setOperation(message.getOperation().getBindingOperationName());
            List<com.eviware.soapui.model.testsuite.AssertionError> soapErrors = new ArrayList<com.eviware.soapui.model.testsuite.AssertionError>();
            valid = valid && soapValidator.validateRequest(message, soapErrors);
            LOGGER.info("Is Request SOAP valid ? " + valid);
            monitor.setRequestSoapErrors(soapErrors);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(soapErrors.toString());
            }
            monitor.setRequestSoapValid(valid);
        } else {
            monitor.setRequestXmlErrors(Arrays.asList(ExtendedWsdlValidator
                    .convertErrors(errors)));
            LOGGER.debug("XML Errors : " + errors);
        }
        monitor.setRequest(requestBodyContent);
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
            HttpServletResponse response, OutputStream out,
            SoapValidator soapValidator, SoapRequestMonitor monitor)
            throws IOException {
        boolean valid = false;
        String responseBodyContent = new String(
                ((BufferedHttpResponseWrapper) response).getBuffer());

        ProxyMonitor proxyResult = Requests.getProxyMonitor(request);

        if (proxyResult != null && proxyResult.getResponseCode() == HttpServletResponse.SC_OK) {
            // 1] XML Well formed ?
            List<XmlError> errors = XmlUtils.validateXml(responseBodyContent);
            valid = (errors.isEmpty());
            monitor.setResponseXmlValid(valid);
            LOGGER.info("Is Response XML valid ? " + errors.isEmpty());

            // 2] Soap Valid ?
            if (valid && (soapValidator != null)) {
                List<com.eviware.soapui.model.testsuite.AssertionError> soapErrors = new ArrayList<com.eviware.soapui.model.testsuite.AssertionError>();
                SoapMessage requestMessage = (SoapMessage) request
                        .getAttribute("requestMessage");
                if (requestMessage != null) {
                    SoapMessage message = soapValidator.newResponseMessage(
                            responseBodyContent, requestMessage);
                    valid = valid
                            && soapValidator.validateResponse(message, soapErrors);
                    LOGGER.info("Is Response SOAP valid ? " + valid);
                    monitor.setResponseSoapErrors(soapErrors);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(soapErrors.toString());
                    }
                    monitor.setResponseSoapValid(valid);
                } else {
                    LOGGER.info("Cant validate SOAP Response as Request was SOAP invalid");
                }
            } else {
                monitor.setResponseXmlErrors(Arrays.asList(ExtendedWsdlValidator
                        .convertErrors(errors)));
            }
        }
        monitor.setResponse(responseBodyContent);
        return valid;
    }

    private SoapValidator getSoapValidator(HttpServletRequest request) {
        SoapValidator soapValidator;
        String wsdlPath = request.getParameter("wsdl");

        if (!Strings.isNullOrEmpty(wsdlPath)) {
            LOGGER.info("Specific Validation activated by parameter wsdl");
            LOGGER.debug("WSDL=" + wsdlPath);
            soapValidator = SoapValidatorFactory.createSoapValidator(wsdlPath);
        } else {
            LOGGER.info("Default WSDL Validation activated (as no wsdl parameter has been provided)");
            String service = Requests.resolveSoapServiceFromRequest(request);
            soapValidator = SoapValidatorFactory.createSoapValidator(service);
        }
        return soapValidator;
    }

}
