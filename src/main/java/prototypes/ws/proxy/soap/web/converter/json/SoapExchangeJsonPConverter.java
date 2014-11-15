/*
 * Copyright 2014 JL06436S.
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
package prototypes.ws.proxy.soap.web.converter.json;

import java.util.Collection;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 * JSON Processing Converter impl
 *
 * JSON Processing impl is bit too verbose on attribute names.
 *
 * @author JL06436S
 */
public class SoapExchangeJsonPConverter extends JsonPConverter<SoapExchange> {

    @Override
    public String toJsonSummary(SoapExchange soapExchange) {
        if (soapExchange != null) {
            return toJsonSummaryBuilder(soapExchange).build().toString();
        }
        return "";
    }

    public JsonObjectBuilder toJsonSummaryBuilder(SoapExchange soapExchange) {
        if (soapExchange != null) {
            JsonObjectBuilder oBuilder = Json.createObjectBuilder()
                    .add("id", stripNull(soapExchange.getId()))
                    .add("date", stripNull(soapExchange.getDate()))
                    .add("from", stripNull(soapExchange.getFrom()))
                    .add("to", stripNull(soapExchange.getTo()))
                    .add("validator", stripNull(soapExchange.getValidatorId()))
                    .add("operation", stripNull(soapExchange.getOperation()))
                    .add("proxy_blocking", soapExchange.isProxyBlocking())
                    .add("proxy_validating", soapExchange.isProxyValidating())
                    .add("proxy_internal_time", soapExchange.getProxyInternalTime())
                    .add("back_end_response_time", soapExchange.getBackEndResponseTime())
                    .add("back_end_response_code", soapExchange.getBackEndResponseCode())
                    .add("request_valid", stripNull(soapExchange.getRequestValid()))
                    .add("request_xml_valid", stripNull(soapExchange.getRequestXmlValid()))
                    .add("request_soap_valid", stripNull(soapExchange.getRequestSoapValid()))
                    .add("response_valid", stripNull(soapExchange.getResponseValid()))
                    .add("response_xml_valid", stripNull(soapExchange.getResponseXmlValid()))
                    .add("response_soap_valid", stripNull(soapExchange.getResponseSoapValid()));
            return oBuilder;
        }

        return null;
    }

    @Override
    public String toJsonSummary(Collection<SoapExchange> soapExchanges) {
        String summaries = "[]";
        if (soapExchanges != null && !soapExchanges.isEmpty()) {
            JsonArrayBuilder aBuilder = Json.createArrayBuilder();
            for (SoapExchange soapRequest : soapExchanges) {
                aBuilder.add(toJsonSummaryBuilder(soapRequest));
            }
            summaries = aBuilder.build().toString();
        }
        return summaries;
    }

    public String toJson(Collection<SoapExchange> soapExchanges) {
        String exchanges = "[]";
        if (soapExchanges != null && !soapExchanges.isEmpty()) {
            JsonArrayBuilder aBuilder = Json.createArrayBuilder();
            for (SoapExchange soapRequest : soapExchanges) {
                aBuilder.add(toJson(soapRequest));
            }
            exchanges = aBuilder.build().toString();
        }
        return exchanges;
    }

    public String toJson(SoapExchange soapExchange) {
        String stringExchange = "";
        if (soapExchange != null) {
            JsonObjectBuilder oBuidler = Json.createObjectBuilder();
            oBuidler.add("id", stripNull(soapExchange.getId()))
                    .add("date", stripNull(soapExchange.getDate()))
                    .add("from", stripNull(soapExchange.getFrom()))
                    .add("to", stripNull(soapExchange.getTo()))
                    .add("validator", stripNull(soapExchange.getValidatorId()))
                    .add("operation", stripNull(soapExchange.getOperation()))
                    .add("proxy_internal_time", stripNull(soapExchange.getProxyInternalTime()))
                    // request
                    .add("front_end_request", stripNull(soapExchange.getFrontEndRequestAsXML()))
                    .add("front_end_request_headers", formatJsonMap(soapExchange.getFrontEndRequestHeaders()))
                    .add("request_errors", formatJsonList(soapExchange.getRequestErrors()))
                    .add("request_xml_errors", formatJsonList(soapExchange.getRequestXmlErrors()))
                    .add("request_soap_errors", formatJsonList(soapExchange.getRequestSoapErrors()))
                    /// request validation
                    .add("request_valid", stripNull(soapExchange.getRequestValid()))
                    .add("request_xml_valid", stripNull(soapExchange.getRequestXmlValid()))
                    .add("request_soap_valid", stripNull(soapExchange.getRequestSoapValid()))
                    // proxy request
                    .add("proxy_request", stripNull(soapExchange.getProxyRequest()))
                    .add("proxy_request_headers", formatJsonMap(soapExchange.getProxyRequestHeaders()))
                    // proxy response
                    .add("proxy_response", stripNull(soapExchange.getProxyResponse()))
                    .add("proxy_response_code", stripNull(soapExchange.getProxyResponseCode()))
                    .add("proxy_response_headers", formatJsonMap(soapExchange.getProxyResponseHeaders()))
                    .add("back_end_response_time", soapExchange.getBackEndResponseTime())
                    .add("back_end_response_code", stripNull(soapExchange.getBackEndResponseCode()))
                    .add("back_end_response", stripNull(soapExchange.getBackendResponseAsXML()))
                    .add("back_end_response_headers", formatJsonMap(soapExchange.getBackendResponseHeaders()))
                    .add("response_errors", formatJsonList(soapExchange.getResponseErrors()))
                    .add("response_xml_errors", formatJsonList(soapExchange.getResponseXmlErrors()))
                    .add("response_soap_errors", formatJsonList(soapExchange.getResponseSoapErrors()))
                    .add("response_valid", stripNull(soapExchange.getResponseValid()))
                    .add("response_xml_valid", stripNull(soapExchange.getResponseXmlValid()))
                    .add("response_soap_valid", stripNull(soapExchange.getResponseSoapValid())
                    );
            return oBuidler.build().toString();
        }
        return stringExchange;
    }
}
