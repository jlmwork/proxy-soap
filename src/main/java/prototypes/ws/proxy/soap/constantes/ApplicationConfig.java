/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
}
