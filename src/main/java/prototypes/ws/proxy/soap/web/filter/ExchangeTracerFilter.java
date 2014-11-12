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
        long start = System.currentTimeMillis();
        // Prepare Objects
        MultiReadHttpServletRequest wrappedRequest = Requests.wrap(request);
        CaptureServletResponseWrapper wrappedResponse = Requests.wrap(response);

        // get or create the exchange
        SoapExchange soapExchange = RequestContext.getSoapExchange(wrappedRequest);
        logger.trace("SoapExchange Hashcode : {}", Integer.toHexString(soapExchange.hashCode()));

        // Frontend Request : extract all data from incoming request
        soapExchange.setFrontEndRequest(Streams.getString(wrappedRequest.getInputStream()));
        soapExchange.setFrontEndRequestHeaders(Requests.getRequestHeaders(request));
        soapExchange.setFrom(request.getRemoteAddr());
        soapExchange.setUri(Requests.getTarget(request));

        chain.doFilter(wrappedRequest, wrappedResponse);

        // Backend Exchange
        BackendExchange backendExchange = RequestContext.getBackendExchange(wrappedRequest);
        logger.trace("BackendExchange Hashcode : {}", Integer.toHexString(backendExchange.hashCode()));
        logger.debug("Backend exchange view from Filter : {}", backendExchange);
        // the request
        soapExchange.setProxyRequest(backendExchange.getRequestBody());
        soapExchange.setProxyRequestHeaders(backendExchange.getRequestHeaders());
        // the response
        soapExchange.setBackEndResponseCode(backendExchange.getResponseCode());
        soapExchange.setBackEndResponseHeaders(backendExchange.getResponseHeaders());
        soapExchange.setBackEndResponse(backendExchange.getResponseBody());

        // final return of the proxy
        soapExchange.setProxyResponse(wrappedResponse.getContent());
        soapExchange.setProxyResponseHeaders(wrappedResponse.getHeaders());

        logger.debug("SoapExchange : {}", soapExchange);
        OutputStream out = response.getOutputStream();
        out.write(wrappedResponse.getBuffer());
        logger.debug("response written");
        // save exchange after response has been sent back to client
        long stop = System.currentTimeMillis();
        soapExchange.setProxyInternalTime(stop - start);
        exchangeRepository.save(soapExchange);
        logger.debug("response saved");
    }

}
