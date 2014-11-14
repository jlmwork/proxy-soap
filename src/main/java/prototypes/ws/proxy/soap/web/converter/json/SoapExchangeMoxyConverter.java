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
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 *
 * @author JL06436S
 */
public class SoapExchangeMoxyConverter extends JsonConverter<SoapExchange> {

    public String toJsonSummary(SoapExchange soapExchange) {
        return "";
    }

    public String toJson(Collection<SoapExchange> soapExchanges) {
        if (soapExchanges != null) {

        }
        return "";
    }

    public String toJson(SoapExchange soapExchange) {
        String stringExchange = "";
        if (soapExchange != null) {

        }
        return stringExchange;
    }
}
