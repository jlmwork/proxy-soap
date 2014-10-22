package prototypes.ws.proxy.soap.io;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.monitor.MonitorManager;
import prototypes.ws.proxy.soap.monitor.ProxyMonitor;
import prototypes.ws.proxy.soap.monitor.SoapRequestMonitor;

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

    public static ProxyConfiguration getProxy(ServletContext servletContext) {
        ProxyConfiguration proxy = (ProxyConfiguration) servletContext
                .getAttribute(ProxyConfiguration.KEY);
        // if not found, create it and references it in application scope
        if (proxy == null) {
            proxy = new ProxyConfiguration();
            servletContext.setAttribute(ProxyConfiguration.KEY, proxy);
        }
        return proxy;
    }

    public static MonitorManager getMonitorManager(ServletContext servletContext) {
        MonitorManager manager = (MonitorManager) servletContext
                .getAttribute(MonitorManager.KEY);
        if (manager == null) {
            manager = new MonitorManager(getProxy(servletContext));
            servletContext.setAttribute(MonitorManager.KEY, manager);
        }
        return manager;
    }

    public static SoapRequestMonitor getRequestMonitor(
            ServletContext servletContext, HttpServletRequest request) {
        SoapRequestMonitor monitor = (SoapRequestMonitor) request
                .getAttribute(SoapRequestMonitor.KEY);
        if (monitor == null) {
            monitor = getMonitorManager(servletContext).monitor();
            request.setAttribute(SoapRequestMonitor.KEY, monitor);
        }
        return monitor;
    }

    public static ProxyMonitor getProxyMonitor(HttpServletRequest request) {
        ProxyMonitor proxyMonitor = (ProxyMonitor) request.getAttribute(ProxyMonitor.KEY);
        if (proxyMonitor == null) {
            proxyMonitor = new ProxyMonitor();
            request.setAttribute(ProxyMonitor.KEY, proxyMonitor);
        }
        return proxyMonitor;
    }

    public static String getTarget(HttpServletRequest request) {
        return request.getRequestURI().replace(
                request.getContextPath() + "/p/", "");
    }

    public static String resolveSoapServiceFromRequest(
            HttpServletRequest request) {
        return resolveSoapServiceFromURL(Requests.getTarget(request));
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
