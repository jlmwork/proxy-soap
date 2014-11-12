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
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.io.Streams;
import prototypes.ws.proxy.soap.web.context.ApplicationContext;

/**
 *
 * @author jlamande
 */
public class SamplesServlet extends AbstractServlet {

    private ProxyConfiguration proxyConfig;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SamplesServlet.class);

    private final Map<String, Sample> samples = new HashMap<String, Sample>();

    @Override
    public void init() throws ServletException {
        super.init();
        proxyConfig = ApplicationContext.getProxyConfiguration(this.getServletContext());
    }

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!proxyConfig.runInDevMode()) {
            throw new ServletException("This servlet can only be used in development mode.");
        }
        LOGGER.debug("SamplesServlet - PathInfo : {}", request.getPathInfo());
        LOGGER.debug("SamplesServlet - Request URI : {}", request.getRequestURI());
        LOGGER.debug("SamplesServlet - Servlet Path : {}", request.getServletPath());

    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response);
        // return a sample if found
        // Correct URL : /{SampleId}
        String sampleId = request.getPathInfo().substring(1);
        LOGGER.debug("Sample id = {}", sampleId);
        Sample sample = this.samples.get(sampleId);
        if (sample != null) {
            response.setStatus(sample.getCode());
            response.setContentType("text/xml; charset=UTF-8");
            response.getWriter().write(sample.getContent());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
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
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response);
        if (request.getServletPath().equals("/sample")) {
            doGet(request, response);
        } else if (request.getServletPath().equals("/samples")) {
            // create a sample if correct
            //"{\"code\" : 200, \"name\": \"name\", \"content\": \"id\"}"
            try {
                Sample sample = new Sample(Streams.getString(request.getInputStream()));
                this.samples.put(sample.getName(), sample);
                LOGGER.debug("Samples : {}", this.samples);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setHeader("Location", sample.getName());
            } catch (Exception e) {
                LOGGER.error("Error : {} ", e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    public static class Sample {

        private final int code;
        private final String name;
        private final String content;

        public Sample(int code, String name, String content) {
            this.code = code;
            this.name = name;
            this.content = content;
        }

        public Sample(String json) {
            JsonReader reader = Json.createReader(new StringReader(json));
            JsonObject sampleObject = reader.readObject();
            this.code = sampleObject.getInt("code");
            this.name = sampleObject.getString("name");
            this.content = sampleObject.getString("content");
            reader.close();
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return this.toJson();
        }

        public String toJson() {
            JsonObjectBuilder oBuidler = Json.createObjectBuilder();
            oBuidler.add("code", code)
                    .add("name", name)
                    .add("content", content);
            return oBuidler.build().toString();
        }

    }
}
