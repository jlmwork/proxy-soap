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
package prototypes.ws.proxy.soap.web.converter.csv;

import java.io.PrintWriter;
import java.util.List;
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 *
 * @author jlamande
 */
public class CsvConverter {

    String separator = ";";
    StringBuilder sb = new StringBuilder();
    PrintWriter out;

    public CsvConverter(PrintWriter out) {
        this.out = out;
        init();
    }

    public void flush() {
        this.out.println(sb.toString());
        sb = new StringBuilder();
    }

    private void init() {
        // title
        this.append("ID")
                .append("Date")
                .append("From")
                .append("To")
                .append("Request XML Errors")
                .append("Request SOAP Errors")
                .append("Response SOAP errors");
        this.flush();
    }

    public CsvConverter append(SoapExchange s) {
        this.append(s.getId()).append(s.getDate()).append(s.getFrom()).append(s.getTo());
        this.append(s.getRequestXmlErrors());
        this.append(s.getRequestSoapErrors());
        this.append(s.getResponseXmlErrors());
        this.append(s.getResponseSoapErrors());
        return this;
    }

    private String cleanupField(String field) {
        return field.replaceAll(separator, "#");
    }

    public CsvConverter append(String field) {
        sb.append(cleanupField(field)).append(separator);
        return this;
    }

    public CsvConverter append(List<?> field) {
        if (field != null && field.size() > 0) {
            this.append(field.toString());
        } else {
            this.append("");
        }
        return this;
    }

    public CsvConverter append(Object field) {
        if (field != null) {
            this.append(field.toString());
        } else {
            this.append("");
        }
        return this;
    }
}
