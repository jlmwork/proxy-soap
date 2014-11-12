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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorServlet extends AbstractServlet {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ErrorServlet.class);
    /**
     *
     */
    private static final long serialVersionUID = 753782663465493431L;

    /**
     * Recept all request.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug("doRequest");

        Throwable throwable = (Throwable) request
                .getAttribute("javax.servlet.error.exception");
        request.getAttribute("javax.servlet.error.status_code");
        request.getAttribute("javax.servlet.error.servlet_name");
        String error = "";

        if (throwable != null) {
            error = "" + throwable.getMessage();
            Throwable cause = throwable.getCause();
            LOGGER.error("Error during Process : {}", cause);
            if ((cause != null)
                    && (cause.getMessage() != null)) {
                error += " (Cause : " + cause.getMessage() + ")";
            }
        }

        // Error Monitoring
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error);
    }

}
