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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.web.context.ApplicationContext;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;

/**
 *
 * @author jlamande
 */
public class ExchangeServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExchangeServlet.class);

    private SoapExchangeRepository exchangeRepository;

    private Pattern p = Pattern.compile(".*/exchange/([^\\?]+)");

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
                // TODO : use "fields" parameter for field selection
                PrintWriter out = response.getWriter();
                JsonWriter jsonWriter = Json.createWriter(out);
                JsonObjectBuilder oBuidler = Json.createObjectBuilder();
                oBuidler.add("id", stripNull(soapExchange.getId()))
                        .add("date", stripNull(soapExchange.getDate()))
                        .add("from", stripNull(soapExchange.getFrom()))
                        .add("to", stripNull(soapExchange.getUri()))
                        .add("validator", stripNull(soapExchange.getValidatorId()))
                        .add("operation", stripNull(soapExchange.getOperation()))
                        .add("resp_time", soapExchange.getResponseTime())
                        .add("request_content", stripNull(soapExchange.getRequest()))
                        .add("request_headers", formatJsonMap(soapExchange.getRequestHeaders()))
                        .add("request_errors", formatJsonList(soapExchange.getRequestErrors()))
                        .add("request_xml_errors", formatJsonList(soapExchange.getRequestXmlErrors()))
                        .add("request_soap_errors", formatJsonList(soapExchange.getRequestSoapErrors()))
                        .add("request_valid", stripNull(soapExchange.getRequestValid()))
                        .add("request_xml_valid", stripNull(soapExchange.getRequestXmlValid()))
                        .add("request_soap_valid", stripNull(soapExchange.getRequestSoapValid()))
                        .add("response_content", stripNull(soapExchange.getResponse()))
                        .add("response_headers", formatJsonMap(soapExchange.getResponseHeaders()))
                        .add("response_errors", formatJsonList(soapExchange.getResponseErrors()))
                        .add("response_xml_errors", formatJsonList(soapExchange.getResponseXmlErrors()))
                        .add("response_soap_errors", formatJsonList(soapExchange.getResponseSoapErrors()))
                        .add("response_valid", stripNull(soapExchange.getResponseValid()))
                        .add("response_xml_valid", stripNull(soapExchange.getResponseXmlValid()))
                        .add("response_soap_valid", stripNull(soapExchange.getResponseSoapValid())
                        );

                jsonWriter.write(oBuidler.build());
                jsonWriter.close();
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

    private static String stripNull(Object o) {
        if (o == null) {
            return "";
        } else {
            return o.toString();
        }
    }

    private JsonObject formatJsonMap(Map<String, List<String>> map) {
        JsonObjectBuilder oBuilder = Json.createObjectBuilder();
        if (map != null) {
            for (String key : map.keySet()) {
                if (key == null) {
                    oBuilder.add("-", map.get(key).toString());
                } else {
                    oBuilder.add(key, map.get(key).toString());
                }
            }
        }
        return oBuilder.build();
    }

    private JsonArray formatJsonList(List<String> list) {
        JsonArrayBuilder aBuilder = Json.createArrayBuilder();
        if (list != null) {
            for (String obj : list) {
                aBuilder.add(obj);
            }
        }
        return aBuilder.build();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
