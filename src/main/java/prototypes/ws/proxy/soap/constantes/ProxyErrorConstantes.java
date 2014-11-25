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
package prototypes.ws.proxy.soap.constantes;

public class ProxyErrorConstantes {

    public static final String INVALID_CLIENT_CONTENT = "Invalid SOAP content received from client";
    public static final String WSDL_NOT_FOUND = "Proxy: WSDL not found";
    public static final String EMPTY_REQUEST = "Empty request body from client.";
    public static final String EMPTY_RESPONSE = "Empty response received from server : %s";
    public static final String TARGET_IS_EMPTY = "Invalid target uri. Uri is empty";
    public static final String INVALID_TARGET = "Invalid target uri %s";
    public static final String NOT_FOUND = "Not found : %s";
    public static final String INVALID_RESPONSE = "Invalid response received from server : %s";
}
