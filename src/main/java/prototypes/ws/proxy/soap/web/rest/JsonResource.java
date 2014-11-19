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
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@Path("/test")
public class JsonResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SoapExchange createSimpleBean() {
        // use of filtering entity throws a NPE
        // java.lang.NullPointerException
        //  at org.glassfish.jersey.message.filtering.ObjectGraphImpl.getFields(ObjectGraphImpl.java:94)
        //  at org.glassfish.jersey.moxy.internal.MoxyObjectProvider.createSubgraphs(MoxyObjectProvider.java:128)
        // Execution Context :
        // MoxyObjectProvider.createSubgraphs(MoxyObjectProvider.java:128)
        //  path = _persistence_session.sessionLog
        //  entityClass = org.eclipse.persistence.sessions.Session
        // ObjectGraphImpl.getFields(ObjectGraphImpl.java:94)
        //  graph is null
        //
        // when done on a JPA entity
        // => JAXB try to graph _persistence_... fields
        // think will need to build a custom object graph to filter those attributes
        // static way : http://blog.bdoughan.com/2013/03/moxys-object-graphs-inputoutput-partial.html
        // dynamic way : http://blog.bdoughan.com/2013/03/moxys-object-graphs-partial-models-on.html
        return new SoapExchange();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<SoapExchange> roundTrip() {
        List<SoapExchange> list = new ArrayList<SoapExchange>();
        list.add(new SoapExchange());
        list.add(new SoapExchange());
        return list;
    }
}
