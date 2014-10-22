package prototypes.ws.proxy.soap.monitor;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;

public class MonitorManager {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(MonitorManager.class);

    public static final String KEY = "proxy.soap.MONITOR";

    private final LinkedList<SoapRequestMonitor> requests = new LinkedList<SoapRequestMonitor>();
    private final ProxyConfiguration proxy;

    public MonitorManager(ProxyConfiguration proxy) {
        this.proxy = proxy;
    }

    public synchronized SoapRequestMonitor monitor() {
        SoapRequestMonitor request = new SoapRequestMonitor();
        LOGGER.info("Add new request");
        requests.addFirst(request);

        if (requests.size() > proxy.getNbMaxRequests()) {
            requests.removeLast();
        }

        return request;
    }

    public synchronized List<SoapRequestMonitor> getRequests() {
        return requests;
    }

    public synchronized void clear() {
        requests.clear();
    }
}
