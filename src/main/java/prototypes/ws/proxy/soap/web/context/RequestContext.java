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
package prototypes.ws.proxy.soap.web.context;

import javax.servlet.http.HttpServletRequest;
import prototypes.ws.proxy.soap.model.BackendExchange;
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 *
 * @author jlamande
 */
public class RequestContext {

    public static void setBackendExchange(HttpServletRequest request, BackendExchange backendExchange) {
        request.setAttribute(BackendExchange.UID, backendExchange);
    }

    public static BackendExchange getBackendExchange(HttpServletRequest request) {
        // try to get the backend exchange from request
        BackendExchange backendExchange = (BackendExchange) request.getAttribute(BackendExchange.UID);
        // not found
        if (backendExchange == null) {
            // try to get the backend exchange from the soap exchange
            SoapExchange soapExchange = (SoapExchange) request.getAttribute(SoapExchange.UID);

            if (soapExchange != null) {
                backendExchange = soapExchange.createBackendExchange();
            } else {
                // create it
                backendExchange = new BackendExchange(request);
            }
        }
        // references it
        request.setAttribute(BackendExchange.UID, backendExchange);
        return backendExchange;
    }

    public static SoapExchange getSoapExchange(HttpServletRequest request) {
        SoapExchange soapExchange = (SoapExchange) request.getAttribute(SoapExchange.UID);
        if (soapExchange == null) {
            soapExchange = new SoapExchange();
            request.setAttribute(SoapExchange.UID, soapExchange);
        }
        return soapExchange;
    }
}
