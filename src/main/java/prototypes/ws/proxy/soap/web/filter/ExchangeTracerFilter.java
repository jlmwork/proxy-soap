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
import java.io.OutputStream;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import prototypes.ws.proxy.soap.configuration.CaptureExpression;
import prototypes.ws.proxy.soap.configuration.Expression;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.io.Streams;
import prototypes.ws.proxy.soap.model.BackendExchange;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;
import prototypes.ws.proxy.soap.web.context.ApplicationContext;
import prototypes.ws.proxy.soap.web.context.RequestContext;
import prototypes.ws.proxy.soap.web.io.CaptureServletResponseWrapper;
import prototypes.ws.proxy.soap.web.io.MultiReadHttpServletRequest;
import prototypes.ws.proxy.soap.web.io.Requests;

/**
 *
 * @author jlamande
 */
public class ExchangeTracerFilter extends HttpServletFilter {

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

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // TODO : extract start in SopaExchange constructor and stop in prepersist converter (for jpa) or when saving (ofr in memory impl)
        long start = System.currentTimeMillis();
        // Prepare Objects
        MultiReadHttpServletRequest wrappedRequest = Requests.wrap(request);
        CaptureServletResponseWrapper wrappedResponse = Requests.wrap(response);

        // get or create the exchange
        SoapExchange soapExchange = RequestContext.getSoapExchange(wrappedRequest);
        soapExchange.setProxyValidation(proxyConfig.isValidationActive());
        soapExchange.setProxyBlocking(proxyConfig.isInBlockingMode());
        logger.trace("SoapExchange Hashcode : {}", Integer.toHexString(soapExchange.hashCode()));

        // Frontend Request : extract all data from incoming request
        soapExchange.setFrontEndRequest(Streams.getBytes(wrappedRequest.getInputStream()));
        soapExchange.setFrontEndRequestHeaders(Requests.getRequestHeaders(request));
        soapExchange.setFrom(request.getRemoteAddr());
        soapExchange.setTo(Requests.getTarget(request));

        chain.doFilter(wrappedRequest, wrappedResponse);

        // add custom headers response back to the client
        wrappedResponse.addHeader("X-Filtered-By", "proxy-soap");
        wrappedResponse.addHeader("X-Filtered-ID", soapExchange.getId());
        wrappedResponse.addHeader("X-Filtering-Validation", "" + soapExchange.isProxyValidating());
        wrappedResponse.addHeader("X-Filtering-Blocking", "" + soapExchange.isProxyBlocking());
        wrappedResponse.addHeader("X-Filtered-Status", soapExchange.getRequestValid() + " " + soapExchange.getResponseValid());
        // validation
        if (soapExchange.getValidatorId() != null) {
            wrappedResponse.addHeader("X-Validated-By", soapExchange.getValidatorId());
        }

        OutputStream out = response.getOutputStream();
        response.setContentLength(wrappedResponse.getBufferSize());
        out.write(wrappedResponse.getBuffer());
        out.close();
        logger.debug("response written");

        // Backend Exchange
        BackendExchange backendExchange = RequestContext.getBackendExchange(wrappedRequest, false);
        // check if a backend exchange occured.
        // When validation has blocked the front end request, there will be no
        // backend exchange available.
        if (backendExchange != null) {
            // the request
            soapExchange.setProxyRequest(backendExchange.getRequestBody());
            soapExchange.setProxyRequestHeaders(backendExchange.getRequestHeaders());
            // the response
            soapExchange.setBackEndResponseTime(backendExchange.getResponseTime());
            soapExchange.setBackEndResponseCode(backendExchange.getResponseCode());
            soapExchange.setBackEndResponseHeaders(backendExchange.getResponseHeaders());
            soapExchange.setBackEndResponse(backendExchange.getResponseBody());
            // extract response backend time from total proxy time
            start = start + backendExchange.getResponseTime();
        }

        // final return of the proxy
        soapExchange.setProxyResponse(wrappedResponse.getBuffer());
        soapExchange.setProxyResponseHeaders(wrappedResponse.getHeaders());
        soapExchange.setProxyResponseCode(wrappedResponse.getStatus());

        // all fields of soap exchange have been set
        // apply ignore Filters and if one matches, the exchange wont be saved
        for (Expression ce : proxyConfig.getIgnoreExpressions()) {
            if (ce.match(soapExchange)) {
                logger.info("Ignore pattern found '{}' : {}", ce.getName());
                // ignore current exchange
                return;
            }
        }

        // captures on the fly
        for (CaptureExpression ce : proxyConfig.getCaptureExpressions()) {
            String capturedContent = ce.capture(soapExchange);
            logger.debug("Captured expression '{}' : {}", ce.getName(), capturedContent);
            if (capturedContent != null) {
                soapExchange.addCapturedField(ce.getName(), capturedContent);
            }
        }

        // save exchange after response has been sent back to client
        long stop = System.currentTimeMillis();
        soapExchange.setProxyInternalTime(stop - start);
        logger.debug("SoapExchange : {}", soapExchange);
        exchangeRepository.save(soapExchange);
        logger.debug("response saved");
    }

}
