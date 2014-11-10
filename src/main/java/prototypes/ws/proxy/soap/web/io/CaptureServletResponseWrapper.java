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
package prototypes.ws.proxy.soap.web.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jlamande
 */
public class CaptureServletResponseWrapper extends BufferedHttpResponseWrapper {

    private int httpStatus;
    private String errorMsg;
    private int contentLength = -1;
    private Map<String, List<String>> headers = new HashMap<String, List<String>>();

    public CaptureServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void sendError(int sc) throws IOException {
        httpStatus = sc;
        super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        httpStatus = sc;
        errorMsg = msg;
        super.sendError(sc, msg);
    }

    @Override
    public void setStatus(int sc) {
        httpStatus = sc;
        super.setStatus(sc);
    }

    public int getStatus() {
        return httpStatus;
    }

    @Override
    public void setHeader(String headerName, String headerValue) {
        // Could watch for values of interest and do...
        if (headerName != null) {
            List<String> values = headers.get(headerName);
            if (values == null) {
                values = new ArrayList<String>();
            }
            values.add(headerValue);
            headers.put(headerName, values);
        }
        super.setHeader(headerName, headerValue);
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Override
    public void setContentLength(int len) {
        contentLength = len;
        super.setContentLength(len);
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getContentLength() {
        return contentLength;
    }

}
