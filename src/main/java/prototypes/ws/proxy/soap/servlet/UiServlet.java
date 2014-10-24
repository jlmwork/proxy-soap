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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.io.Requests;
import prototypes.ws.proxy.soap.io.Streams;
import prototypes.ws.proxy.soap.monitor.MonitorManager;
import prototypes.ws.proxy.soap.validation.SoapValidatorFactory;

public class UiServlet extends AbstractServlet {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(UiServlet.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final String ACTION_PATH = "ui/action";

    private MonitorManager monitor;

    @Override
    protected void doRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("UiServlet : doRequest");

        // Extract service url
        String action = request.getRequestURI().replace(
                request.getContextPath() + "/" + ACTION_PATH + "/", "");
        request.setAttribute("action", action);

        LOGGER.info("UiServlet : uri = " + action);
        ProxyConfiguration proxy = (ProxyConfiguration) Requests.getProxy(this.getServletContext());
        request.setAttribute("settings", ProxyConfiguration.getKeys());
        request.setAttribute("proxy", proxy);

        request.setAttribute("requestList", monitor.getRequests());
        request.setAttribute("validators", SoapValidatorFactory.getValidators());

        if ("clearRequests".equals(action)) {
            clearRequests(request, response);
            return;
        } else if ("viewWSDL".equals(action)) {
            viewWSDL(request, response);
            return;
        } else if ("config".equals(action)) {
            boolean saved = false;
            for (String key : ProxyConfiguration.getKeys()) {
                if (request.getParameter(key) != null) {
                    proxy.setProperty(key, request.getParameter(key));
                    saved = true;
                }
            }
            if (saved) {
                request.setAttribute("success", "panel-success");
                request.setAttribute("message", "config.saved");
            }
            if (request.getParameter("persist") != null) {
                proxy.persist();
                request.setAttribute("message", "config.persisted");
            }
        }

        LOGGER.info("UiServlet getRequestURI:" + request.getRequestURI()
                + "- contextPath:" + request.getContextPath());
        request.setAttribute("page",
                request.getRequestURI().replace(request.getServletPath(), "")
                .replace(request.getContextPath(), ""));
        request.getRequestDispatcher("/WEB-INF/views/jsp/ui.jsp").forward(
                request, response);
    }

    public void clearRequests(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        monitor.clear();
        response.sendRedirect(request.getContextPath() + "/ui/logs");
    }

    public void viewWSDL(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        try {
            String validator = request.getParameter("validator");
            if (validator != null) {
                String urlStr = SoapValidatorFactory.getValidators()
                        .get(validator).getUrl();
                URL url = new URL(urlStr);
                LOGGER.debug("WSDL Url : " + url);
                response.setContentType("text/xml;charset=UTF-8");
                PrintWriter out = response.getWriter();
                File wsdlFile = new File(url.toURI());
                LOGGER.debug("WSDL File URI : " + wsdlFile.getAbsolutePath());
                if (wsdlFile.exists()) {
                    out.println(Streams
                            .getString(new FileInputStream(wsdlFile)));
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
        } catch (URISyntaxException e) {
            LOGGER.warn("Problem during WSDL url resolving " + e.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }

    @Override
    public void init(ServletConfig sconfig) throws ServletException {
        super.init(sconfig);
        monitor = Requests.getMonitorManager(this.getServletContext());
    }
}
