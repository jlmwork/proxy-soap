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
package prototypes.ws.proxy.soap.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;
import prototypes.ws.proxy.soap.web.context.ApplicationContext;
import prototypes.ws.proxy.soap.web.converter.json.SoapExchangeJsonPConverter;

/**
 *
 * @author jlamande
 */
public class ExchangeServlet extends AbstractServlet {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExchangeServlet.class);

    private SoapExchangeRepository exchangeRepository;

    private final Pattern p = Pattern.compile(".*/exchange/([^\\?]+)");

    @Override
    public void init() throws ServletException {
        super.init();
        exchangeRepository = ApplicationContext.getSoapExchangeRepository(this.getServletContext());
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("Exchanges");

        // Content-negotiation
        // as ajax does not support file download very well
        // the application accept a request parameter called accept
        String pAccept = request.getParameter("accept");
        String hAccept = request.getHeader("Accept");
        String askedFormat = (!Strings.isNullOrEmpty(pAccept))
                ? pAccept : ((!Strings.isNullOrEmpty(hAccept)) ? hAccept : "");
        LOGGER.debug("Asked format : " + askedFormat);

        String exchangeId = getExchangeId(request.getRequestURI());
        LOGGER.debug("Asked exchange : " + exchangeId);
        if (!Strings.isNullOrEmpty(exchangeId)) {
            String fieldsParam = request.getParameter("fields");
            String[] fields = new String[]{};
            if (fieldsParam != null) {
                fields = fieldsParam.split(",");
            }
            SoapExchange soapExchange = exchangeRepository.get(exchangeId, fields);

            if ("application/json".equals(askedFormat.toLowerCase())) {
                response.setHeader("Content-Type", "application/json; charset=UTF-8");
                // TODO : use "fields" parameter for field selection
                PrintWriter out = response.getWriter();
                String soapExchangeJsonStr = (new SoapExchangeJsonPConverter()).toJson(soapExchange);
                out.write(soapExchangeJsonStr);
                out.close();
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found - There is no resource behind the URI.");
        }
    }

    public String getExchangeId(String in) {
        LOGGER.debug("Search exchangeId in {}", in);
        String id = "";
        Matcher m = p.matcher(in);
        if (m.find()) {
            id = m.group(1);
        }
        return id;
    }

}
