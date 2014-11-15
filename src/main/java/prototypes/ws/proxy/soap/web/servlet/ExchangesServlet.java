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
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constantes.ApplicationConfig;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.io.ZipOut;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;
import prototypes.ws.proxy.soap.time.Dates;
import prototypes.ws.proxy.soap.web.context.ApplicationContext;
import prototypes.ws.proxy.soap.web.converter.json.SoapExchangeJsonPConverter;

/**
 *
 * @author jlamande
 */
public class ExchangesServlet extends AbstractServlet {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExchangesServlet.class);

    private SoapExchangeRepository exchangeRepository;

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

        if ("text/csv".equals(askedFormat.toLowerCase())) {
            respondInCsv(response);
        } else if ("application/zip".equals(askedFormat.toLowerCase())) {
            respondInZip(response);

        } else if ("application/json".equals(askedFormat.toLowerCase())) {
            respondInJson(response);
        }
    }

    private void respondInZip(HttpServletResponse response) throws IOException {
        Collection<SoapExchange> soapExchanges = exchangeRepository.listWithoutContent();
        LOGGER.debug("ZIP format");
        response.setContentType("application/zip");
        Cookie cookie = new Cookie("fileDownload", "true");
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setHeader("Content-Description", "File Transfer");
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + generateFilename("zip"));
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Pragma", "public");
        ZipOut zipOut = new ZipOut(response.getOutputStream());
        PrintWriter writer = zipOut.getFileWriter(generateFilename("csv"));
        CsvWriter csvBuilder = new CsvWriter(writer);
        LOGGER.debug("Export " + soapExchanges.size() + " soapExchanges");
        for (SoapExchange soapRequest : soapExchanges) {
            csvBuilder.append(soapRequest).flush();
        }
        zipOut.closeFileWriter();
        zipOut.addDirToZipStream(ApplicationConfig.EXCHANGES_STORAGE_PATH, new String[]{"xml"});
        zipOut.finish();
    }

    private void respondInJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Collection<SoapExchange> soapExchanges = exchangeRepository.listWithoutContent();
        // TODO : use "fields" parameter for field selection
        PrintWriter out = response.getWriter();
        out.write((new SoapExchangeJsonPConverter()).toJsonSummary(soapExchanges));
        out.close();
    }

    private void respondInCsv(HttpServletResponse response) throws IOException {
        Collection<SoapExchange> soapExchanges = exchangeRepository.listWithoutContent();
        LOGGER.debug("CSV format");
        response.setContentType("text/csv;charset=UTF-8");
        Cookie cookie = new Cookie("fileDownload", "true");
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setHeader("Content-Description", "File Transfer");
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + generateFilename("csv"));
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Pragma", "public");
        PrintWriter out = response.getWriter();
        try {
            CsvWriter csvBuilder = new CsvWriter(out);
            LOGGER.debug("Export " + soapExchanges.size() + " soapExchanges");
            for (SoapExchange soapRequest : soapExchanges) {
                csvBuilder.append(soapRequest).flush();
            }
        } finally {
            out.close();
        }
    }

    private String generateFilename(String extension) {
        StringBuilder sb = new StringBuilder("exchanges_export_");
        sb.append(Dates.getFormattedDate(Dates.YYYYMMDD_HHMMSS));
        sb.append(".").append(extension);
        return sb.toString();
    }

    private static class CsvWriter {

        String separator = ";";
        StringBuilder sb = new StringBuilder();
        PrintWriter out;

        CsvWriter(PrintWriter out) {
            this.out = out;
            init();
        }

        public void flush() {
            this.out.println(sb.toString());
            sb = new StringBuilder();
        }

        private void init() {
            // title
            this.append("ID")
                    .append("Date")
                    .append("From")
                    .append("To")
                    .append("Request XML Errors")
                    .append("Request SOAP Errors")
                    .append("Response SOAP errors");
            this.flush();
        }

        public CsvWriter append(SoapExchange s) {
            this.append(s.getId()).append(s.getDate()).append(s.getFrom()).append(s.getTo());
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

        public CsvWriter append(String field) {
            sb.append(cleanupField(field)).append(separator);
            return this;
        }

        public CsvWriter append(List<?> field) {
            if (field != null && field.size() > 0) {
                this.append(field.toString());
            } else {
                this.append("");
            }
            return this;
        }

        public CsvWriter append(Object field) {
            if (field != null) {
                this.append(field.toString());
            } else {
                this.append("");
            }
            return this;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        exchangeRepository.removeAll();
        LOGGER.info("Exchanges successfully deleted");
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

}
