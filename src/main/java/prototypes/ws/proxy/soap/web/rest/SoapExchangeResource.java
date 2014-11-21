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
package prototypes.ws.proxy.soap.web.rest;

/**
 *
 * @author jlamande
 */
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;
import prototypes.ws.proxy.soap.web.context.ApplicationContext;
import prototypes.ws.proxy.soap.web.servlet.SamplesServlet;

/**
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@Path("/exchange")
public class SoapExchangeResource {

    private final SoapExchangeRepository exchangeRepository;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SamplesServlet.class);

    public SoapExchangeResource(@Context ServletContext context) {
        LOGGER.debug("SampleResource creation.");
        exchangeRepository = ApplicationContext.getSoapExchangeRepository(context);
    }

    @GET
    @Path("/{exchangeId}")
    @Produces({MediaType.APPLICATION_JSON + ";charset=utf-8", MediaType.APPLICATION_XML + ";charset=utf-8"})
    public SoapExchange getExchange(@PathParam("exchangeId") String exchangeId) {
        LOGGER.debug("Ask for exchange : {}", exchangeId);
        SoapExchange soapExchange = exchangeRepository.get(exchangeId, null);
        if (soapExchange == null) {
            LOGGER.debug("Exchange {} not found !", exchangeId);
        } else {
            LOGGER.debug("Soap Exchange : {}", soapExchange);
        }
        return soapExchange;
    }

    // no id
    @GET
    @Produces({MediaType.APPLICATION_JSON + ";charset=utf-8", MediaType.APPLICATION_XML + ";charset=utf-8"})
    public Collection<SoapExchange> getExchanges() {
        LOGGER.debug("Ask for exchanges");
        Collection<SoapExchange> soapExchanges = exchangeRepository.listWithoutContent();
        if (soapExchanges == null) {
            LOGGER.debug("No Exchange found !");
        } else {
            LOGGER.debug("{} Soap Exchange found", soapExchanges.size());
        }
        return soapExchanges;
    }
}
