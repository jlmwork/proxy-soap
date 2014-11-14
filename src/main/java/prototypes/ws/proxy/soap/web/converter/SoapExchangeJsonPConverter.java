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
package prototypes.ws.proxy.soap.web.converter;

import java.util.Collection;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 *
 * @author JL06436S
 */
public class SoapExchangeJsonPConverter extends JsonConverter<SoapExchange> {

    public String toJsonShort(SoapExchange soapExchange) {
        return "";
    }

    public String toJson(Collection<SoapExchange> soapExchanges) {
        if (soapExchanges != null) {
            JsonObjectBuilder oBuilder = Json.createObjectBuilder();
            JsonArrayBuilder aBuilder = Json.createArrayBuilder();
            for (SoapExchange soapExchange : soapExchanges) {
                aBuilder.add(Json.createObjectBuilder()
                        .add("id", stripNull(soapExchange.getId()))
                        .add("date", stripNull(soapExchange.getDate()))
                        .add("from", stripNull(soapExchange.getFrom()))
                        .add("to", stripNull(soapExchange.getUri()))
                        .add("validator", stripNull(soapExchange.getValidatorId()))
                        .add("operation", stripNull(soapExchange.getOperation()))
                        .add("resp_time", soapExchange.getBackEndResponseTime())
                        .add("request_valid", stripNull(soapExchange.getRequestValid()))
                        .add("request_xml_valid", stripNull(soapExchange.getRequestXmlValid()))
                        .add("request_soap_valid", stripNull(soapExchange.getRequestSoapValid()))
                        .add("response_valid", stripNull(soapExchange.getResponseValid()))
                        .add("response_xml_valid", stripNull(soapExchange.getResponseXmlValid()))
                        .add("response_soap_valid", stripNull(soapExchange.getResponseSoapValid()))
                );
            }
            return aBuilder.build().toString();
        }
        return "";
    }

    public String toJson(SoapExchange soapExchange) {
        String stringExchange = "";
        if (soapExchange != null) {
            JsonObjectBuilder oBuidler = Json.createObjectBuilder();
            oBuidler.add("id", stripNull(soapExchange.getId()))
                    .add("date", stripNull(soapExchange.getDate()))
                    .add("from", stripNull(soapExchange.getFrom()))
                    .add("to", stripNull(soapExchange.getUri()))
                    .add("validator", stripNull(soapExchange.getValidatorId()))
                    .add("operation", stripNull(soapExchange.getOperation()))
                    // request
                    .add("request_content", stripNull(soapExchange.getFrontEndRequestAsXML()))
                    .add("request_headers", formatJsonMap(soapExchange.getFrontEndRequestHeaders()))
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
                    .add("backend_response_time", soapExchange.getBackEndResponseTime())
                    .add("backend_response_code", stripNull(soapExchange.getBackEndResponseCode()))
                    .add("backend_response_content", stripNull(soapExchange.getBackendResponseAsXML()))
                    .add("backend_response_headers", formatJsonMap(soapExchange.getBackendResponseHeaders()))
                    /// response validation
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
