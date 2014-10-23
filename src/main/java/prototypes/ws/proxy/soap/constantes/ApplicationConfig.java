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

/**
 *
 * @author JL06436S
 */
public class ApplicationConfig {

    public static String PROP_VALIDATION = "proxy.soap.validate";
    public static String PROP_BLOCKING_MODE = "proxy.soap.blockingmode";
    // changed the value of following key from "proxy.soap.schemadir" to force
    // reconfiguration
    // on preivous installed proxies
    public static String PROP_WSDL_DIRS = "proxy.soap.wsdls";
    public static String PROP_MAX_REQUESTS = "proxy.soap.maxrequests";

    public static String PROP_ENV = "proxy.soap.env";

    public static Integer ENV_PROD = 0;
    public static Integer ENV_DEV = 1;
}
