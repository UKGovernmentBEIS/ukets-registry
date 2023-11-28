package gov.uk.ets.commons.cloudwatch.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@Log4j2
public class HealthMetricsExportConfiguration {

    private InetAddress ip;

    /**
     * Used to export application's health as a metric (UP:1, DOWN:-1)
     */
    @Autowired
    public HealthMetricsExportConfiguration(
    		MeterRegistry registry, HealthEndpoint healthEndpoint, BuildProperties buildProperties) {
    	String appName = buildProperties != null ? buildProperties.getName() : "";
    	String hostname = "";
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException e) {
            log.warn("no hostname found");
        }
        Gauge.builder("health", healthEndpoint, this::getStatusCode)
        .strongReference(true)
        .tag("appName", appName)
        .tag("hostName", hostname)
            .register(registry);
    }

    private int getStatusCode(HealthEndpoint health) {       
        Status status = health.health().getStatus();
        if (Status.UP.equals(status)) {
            return 1;
        }
        if (Status.OUT_OF_SERVICE.equals(status)) {
            return -2;
        }
        if (Status.DOWN.equals(status)) {
            return -1;
        }
        return 0;
    }

}