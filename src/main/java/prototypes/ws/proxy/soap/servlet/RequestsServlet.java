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
package prototypes.ws.proxy.soap.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.context.ApplicationContext;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.io.SoapExchange;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;
import prototypes.ws.proxy.soap.time.Dates;

/**
 *
 * @author jlamande
 */
public class RequestsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RequestsServlet.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("Requests");

        // Content-negotiation
        // as ajax does not support file download very well
        // the application accept a request parameter called accept
        String pAccept = request.getParameter("accept");
        String hAccept = request.getHeader("Accept");
        String askedFormat = (!Strings.isNullOrEmpty(pAccept))
                ? pAccept : ((!Strings.isNullOrEmpty(hAccept)) ? hAccept : "");
        LOGGER.debug("Asked format : " + askedFormat);

        SoapExchangeRepository repository = ApplicationContext.getSoapExchangeRepository(this.getServletContext());
        List<SoapExchange> soapExchanges = repository.list();

        if ("text/csv".equals(askedFormat.toLowerCase())) {
            LOGGER.debug("CSV format");
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader(null, null);
            Cookie cookie = new Cookie("fileDownload", "true");
            cookie.setPath("/");
            response.addCookie(cookie);
            response.setHeader("Content-Description", "File Transfer");
            response.setHeader("Content-Type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + generateFilename());
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Pragma", "public");
            PrintWriter out = response.getWriter();
            try {
                String csvTitle = "ID;Date;From;To;Request XML Errors;Request SOAP Errors;Response SOAP errors";
                out.println((new CsvBuilder()).append(csvTitle).toString());
                LOGGER.debug("Export " + soapExchanges.size() + " soapRequests");
                for (SoapExchange soapRequest : soapExchanges) {
                    out.println((new CsvBuilder()).append(soapRequest).toString());
                }
            } finally {
                out.close();
            }
        } else if ("application/json".equals(askedFormat.toLowerCase())) {
            //JsonGenerator jg = jsonF.createJsonGenerator(new File("result.json"), JsonEncoding.UTF8);
            /*JsonObject model = Json.createObjectBuilder()
             .add("firstName", "Duke")
             .add("lastName", "Java")
             .add("age", 18)
             .add("streetAddress", "100 Internet Dr")
             .add("city", "JavaTown")
             .add("state", "JA")
             .add("postalCode", "12345")
             .add("phoneNumbers", Json.createArrayBuilder()
             .add(Json.createObjectBuilder()
             .add("type", "mobile")
             .add("number", "111-111-1111"))
             .add(Json.createObjectBuilder()
             .add("type", "home")
             .add("number", "222-222-2222")))
             .build();*/
            PrintWriter out = response.getWriter();
            JsonWriter jsonWriter = Json.createWriter(out);
            LOGGER.debug("Export " + soapExchanges.size() + " soapRequests");
            JsonObjectBuilder oBuilder = Json.createObjectBuilder();
            JsonArrayBuilder aBuilder = Json.createArrayBuilder();
            for (SoapExchange soapRequest : soapExchanges) {
                aBuilder.add(Json.createObjectBuilder()
                        .add("ID", soapRequest.getId())
                        .add("Date", soapRequest.getDate())
                        .add("From", soapRequest.getFrom())
                        .add("To", soapRequest.getUri())
                );
            }
            oBuilder.add("requests", aBuilder.build());
            jsonWriter.write(oBuilder.build());
            jsonWriter.close();
            out.close();
        } else {
            request.setAttribute("requestList", soapExchanges);
            request.getRequestDispatcher("/WEB-INF/views/jsp/requests.jsp").forward(request, response);
        }
    }

    private String generateFilename() {
        StringBuilder sb = new StringBuilder("requests_export_");
        sb.append(Dates.getFormattedDate(Dates.YYYYMMDD_HHMMSS));
        sb.append(".csv");
        return sb.toString();
    }

    private static class CsvBuilder {

        String separator = ";";
        StringBuilder sb = new StringBuilder();

        public CsvBuilder append(SoapExchange s) {
            this.append(s.getId()).append(s.getDate()).append(s.getFrom()).append(s.getUri());
            this.append(s.getRequestXmlErrors());
            this.append(s.getRequestSoapErrors());
            this.append(s.getResponseXmlErrors());
            this.append(s.getResponseSoapErrors());
            return this;
        }

        private String cleanupField(String field) {
            String cleanField = field.replaceAll(separator, "#");
            return cleanField;
        }

        public CsvBuilder append(String field) {
            sb.append(cleanupField(field)).append(separator);
            return this;
        }

        public CsvBuilder append(List<?> field) {
            if (field != null && field.size() > 0) {
                this.append(field.toString());
            } else {
                this.append("");
            }
            return this;
        }

        public CsvBuilder append(Object field) {
            if (field != null) {
                this.append(field.toString());
            } else {
                this.append("");
            }
            return this;
        }

        public String toString() {
            return sb.toString();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

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
