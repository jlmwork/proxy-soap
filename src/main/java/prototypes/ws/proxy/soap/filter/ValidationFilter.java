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
public class ValidationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ValidationFilter.class);

    private FilterConfig config;

    private ProxyConfiguration proxy;

    /**
     * @see Filter#init(FilterConfig)
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        // retrieve config object from application scope
        proxy = Requests.getProxy(config.getServletContext());
        this.config = config;
    }

    /**
     *
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     *
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpRequest.getSession().getServletContext();

        if (proxy.isValidationActive() && "POST".equals(httpRequest.getMethod())) {
            SoapRequestMonitor monitor = Requests.getRequestMonitor(
                    this.config.getServletContext(), httpRequest);
            monitor.setUri(Requests.getTarget(httpRequest));
            monitor.setFrom(httpRequest.getRemoteAddr());
            LOGGER.info("Validation is active.");

            // Prepare Objects
            // wraps request for allowing to read request body more than once
            // This wrapping must occur in the first called ServletFilter
            httpRequest = new MultiReadHttpServletRequest(httpRequest);
            // wraps response for post processing
            httpResponse = new BufferedHttpResponseWrapper(httpResponse);
            OutputStream out = response.getOutputStream();

            // creates a SoapValidator
            SoapValidator validator = getSoapValidator(httpRequest);

            // 1] Request validation
            boolean requestValid = validateInput(httpRequest, validator,
                    monitor);
            if (!requestValid && proxy.isInBlockingMode()) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Request message invalid");
                return;
            }

            // 2] pass the request along the filter chain
            long start = System.currentTimeMillis();
            chain.doFilter(httpRequest, httpResponse);
            long stop = System.currentTimeMillis();
            monitor.setResponseTime(stop - start);

            // 3] Response validation
            boolean responseValid = validateOutput(httpRequest, httpResponse,
                    out, validator, monitor);
            if (!responseValid && proxy.isInBlockingMode()) {
                LOGGER.info("Proxy is in blocking mode.");
                httpResponse.sendError(HttpServletResponse.SC_BAD_GATEWAY,
                        "Response message invalid");
                return;
            }
            if (requestValid && responseValid && proxy.isIgnoreValidRequests()) {
                Requests.getMonitorManager(this.config.getServletContext()).getRequests().remove(monitor);
            }
            // send response back to the client
            httpResponse.addHeader("X-Filtered-By", "proxy-soap");
            out.write(monitor.getResponse().getBytes());
        } else {
            // pass the request along the filter chain
            chain.doFilter(httpRequest, httpResponse);
        }
    }

    /**
     * called before the Filter chain if validation is active
     *
     * @param httpRequest
     * @param soapValidator
     * @return
     * @throws IOException
     */
    private boolean validateInput(HttpServletRequest httpRequest,
            SoapValidator soapValidator, SoapRequestMonitor monitor)
            throws IOException {
        String requestBodyContent = new String(Streams.getBytes(httpRequest
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
            httpRequest.setAttribute("requestMessage", message);
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
     * @param httpResponse
     * @param out
     * @param soapValidator
     * @throws IOException
     */
    private boolean validateOutput(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse, OutputStream out,
            SoapValidator soapValidator, SoapRequestMonitor monitor)
            throws IOException {
        boolean valid = false;
        String responseBodyContent = new String(
                ((BufferedHttpResponseWrapper) httpResponse).getBuffer());

        ProxyMonitor proxyResult = Requests.getProxyMonitor(httpRequest);

        if (proxyResult != null && proxyResult.getResponseCode() == HttpServletResponse.SC_OK) {
            // 1] XML Well formed ?
            List<XmlError> errors = XmlUtils.validateXml(responseBodyContent);
            valid = (errors.isEmpty());
            monitor.setResponseXmlValid(valid);
            LOGGER.info("Is Response XML valid ? " + errors.isEmpty());

            // 2] Soap Valid ?
            if (valid && (soapValidator != null)) {
                List<com.eviware.soapui.model.testsuite.AssertionError> soapErrors = new ArrayList<com.eviware.soapui.model.testsuite.AssertionError>();
                SoapMessage requestMessage = (SoapMessage) httpRequest
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

    /**
     * @see Filter#destroy()
     */
    @Override
    public void destroy() {
    }

}
