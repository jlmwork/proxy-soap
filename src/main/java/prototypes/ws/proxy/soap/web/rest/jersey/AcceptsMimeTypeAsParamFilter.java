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
package prototypes.ws.proxy.soap.web.rest.jersey;

import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 * Executes on all the requests prior Jersey matching the resources. It allows
 * to use the request parameter "accept" as an alternative to the HTTP header
 * Accept.
 *
 *
 * </p>
 * Technically, if present, the request parameter "accept" will override the
 * header "accept". It's a way for simplifying testing. Inspired by :
 * http://techblog.chegg.com/2014/06/06/how-to-teach-jersey-to-speak-csv/
 *
 * Thanks to Sergey.
 * </p>
 *
 * @author Sergey Melnik
 * @author jlamande
 */
@PreMatching
@Provider
@Priority(value = Integer.MAX_VALUE - 1000)
public class AcceptsMimeTypeAsParamFilter implements ContainerRequestFilter {

    private static final String acceptParameterName = "accept";

    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        UriInfo uriInfo = crc.getUriInfo();
        String formatRequested = uriInfo.getQueryParameters().getFirst(acceptParameterName);
        if (formatRequested != null) {
            crc.getHeaders().putSingle(HttpHeaders.ACCEPT, formatRequested);
        }
    }
}
