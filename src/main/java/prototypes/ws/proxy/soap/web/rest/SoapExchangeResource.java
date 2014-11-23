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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constantes.ApplicationConfig;
import prototypes.ws.proxy.soap.io.ZipOut;
import prototypes.ws.proxy.soap.model.SoapExchange;
import prototypes.ws.proxy.soap.repository.SoapExchangeRepository;
import prototypes.ws.proxy.soap.time.Dates;
import prototypes.ws.proxy.soap.web.context.ApplicationContext;
import prototypes.ws.proxy.soap.web.converter.csv.CsvConverter;
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

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getExchangesAsZip(@QueryParam("type") final String type) {

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                if (type != null && type.equals("zip")) {
                    Collection<SoapExchange> soapExchanges = exchangeRepository.listWithoutContent();
                    ZipOut zipOut = new ZipOut(os);
                    PrintWriter writer = zipOut.getFileWriter(generateFilename("csv"));
                    CsvConverter csvConverter = new CsvConverter(writer);
                    LOGGER.debug("Export {} soapExchanges", soapExchanges.size());
                    for (SoapExchange soapRequest : soapExchanges) {
                        csvConverter.append(soapRequest).flush();
                    }
                    zipOut.closeFileWriter();
                    zipOut.addDirToZipStream(ApplicationConfig.EXCHANGES_STORAGE_PATH, new String[]{"xml", "xml.gz"});
                    zipOut.finish();
                } else {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                    for (int i = 0; i < 1000; i++) {
                        writer.write("testtesttesttesttesttesttesttesttesttest" + "\n");
                        System.out.println("writing a chunk");
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        writer.flush();
                    }
                }
            }
        };

        // could use Filter for more flexible Cache Control :
        // http://alex.nederlof.com/blog/2013/07/28/caching-using-annotations-with-jersey/
        if (type != null && type.equals("zip")) {
            return Response.ok(stream)
                    .header("Content-Type", "application/zip")
                    .header("Content-Description", "File Transfer")
                    .header("Content-Transfer-Encoding", "binary")
                    .header("Expires", "0")
                    .header("Cache-Control", "must-revalidate")
                    .header("Pragma", "public")
                    .header("content-disposition", "attachment; filename = " + generateFilename("zip")).build();
        } else {
            return Response.ok(stream)
                    .header("Content-Type", MediaType.TEXT_PLAIN)
                    //.header("Content-Description", "File Transfer")
                    //.header("content-disposition", "inline; filename = " + generateFilename("txt"))
                    .build();
        }
    }

    private String generateFilename(String extension) {
        StringBuilder sb = new StringBuilder("exchanges_export_");
        sb.append(Dates.getFormattedDate(Dates.YYYYMMDD_HHMMSS));
        sb.append(".").append(extension);
        return sb.toString();
    }
}
