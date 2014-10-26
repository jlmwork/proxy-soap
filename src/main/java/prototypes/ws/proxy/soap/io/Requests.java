package prototypes.ws.proxy.soap.io;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constantes.ProxyErrorConstantes;

public class Requests {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Requests.class);

    /**
     * Get complete host, e.g. <scheme>://<serverName>:<port>
     *
     * @return
     */
    public static String getHost(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":"
                + request.getServerPort();
    }

    public static String getTarget(HttpServletRequest request) {
        return request.getRequestURI().replace(
                request.getContextPath() + "/p/", "");
    }

    public static String resolveSoapServiceFromRequest(
            HttpServletRequest request) {
        return resolveSoapServiceFromURL(Requests.getTarget(request));
    }

    public static URL resolveTargetUrl(HttpServletRequest request) {
        String uri = Requests.getTarget(request);
        if (Strings.isNullOrEmpty(uri)) {
            throw new IllegalStateException(
                    ProxyErrorConstantes.TARGET_IS_EMPTY);
        }
        if (!uri.matches("^\\w+://.*")) {
            LOGGER.debug("URI doesnt match URL pattern. So add current request host");
            uri = Requests.getHost(request) + "/" + uri;
        }

        // TODO :
        LOGGER.debug("Target uri : " + uri);
        URL targetUrl;
        try {
            targetUrl = new URL(uri);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format(
                    ProxyErrorConstantes.INVALID_TARGET, uri));
        }
        return targetUrl;
    }

    public static String resolveSoapServiceFromURL(String url) {
        String service = "";
        LOGGER.debug("Resolve service in URL : " + url);
        if (!Strings.isNullOrEmpty(url)) {
            int pos = url.lastIndexOf("/");
            if (pos == -1) {
                pos = url.lastIndexOf("\\");
            }
            if ((pos != -1) && ((pos + 1) < url.length())) {
                service = url.substring(pos + 1);
            }
            if (service.toLowerCase().endsWith(".wsdl")) {
                service = service.substring(0, service.length() - 5);
            }
        }
        LOGGER.debug("Resolved : '" + service + "'");
        return service;
    }

    public static boolean isHttpPath(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return false;
        }
        str = str.toLowerCase();

        return str.startsWith("http:/") || str.startsWith("https:/");
    }

}
