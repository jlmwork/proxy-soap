/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes.ws.proxy.soap.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.io.Requests;
import prototypes.ws.proxy.soap.io.Strings;
import prototypes.ws.proxy.soap.monitor.MonitorManager;
import prototypes.ws.proxy.soap.monitor.SoapRequestMonitor;

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
        // Content-negotiation
        String hAccept = request.getHeader("Accept");
        String format = (Strings.isNullOrEmpty(hAccept)) ? hAccept : "";

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader(null, null);
        Cookie cookie = new Cookie("fileDownload", "true");
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setHeader("Content-Description", "File Transfer");
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=requests_export.csv");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Pragma", "public");
        PrintWriter out = response.getWriter();
        try {
            String csvTitle = "ID;Date;From;To;URI;Request XML Errors;Request SOAP Errors;Response SOAP errors";
            out.println((new CsvBuilder()).append(csvTitle).toString());
            MonitorManager monitor = (MonitorManager) Requests.getMonitorManager(this.getServletContext());
            List<SoapRequestMonitor> soapRequests = monitor.getRequests();
            LOGGER.debug("Export " + soapRequests.size() + " soapRequests");
            for (SoapRequestMonitor soapRequest : soapRequests) {
                out.println((new CsvBuilder()).append(soapRequest).toString());
            }
        } finally {
            out.close();
        }
    }

    private static class CsvBuilder {

        String separator = ";";
        StringBuilder sb = new StringBuilder();

        public CsvBuilder append(SoapRequestMonitor s) {
            this.append(s.getId()).append(s.getDate()).append(s.getFrom()).append(s.getUri());
            this.append(s.getRequestXmlErrors());
            this.append(s.getRequestSoapErrors());
            this.append(s.getResponseXmlErrors());
            this.append(s.getResponseSoapErrors());
            return this;
        }

        public CsvBuilder append(String field) {
            sb.append(field).append(separator);
            return this;
        }

        public CsvBuilder append(List<?> field) {
            if (field != null && field.size() > 0) {
                sb.append(field.toString());
            }
            sb.append(separator);
            return this;
        }

        public CsvBuilder append(Object field) {
            if (field != null) {
                sb.append(field.toString());
            }
            sb.append(separator);
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
