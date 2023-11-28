package gov.uk.ets.commons.logging;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;

/**
 * Super class providing some common capabilities.
 */
@Log4j2
public abstract class MDCLoggingHandler {

    private static final String PROP_SERVER_PORT = "server.port";
    private Environment environment;

    /**
     * Adds a common join point processing method.
     * @param proceedingJoinPoint the join point
     * @param mdc the mdc wrapper
     * @param start the time
     * @return the actual object returned from the method
     * @throws Throwable if an exception occurred
     */
    public Object getProceedingJoinPointResult(ProceedingJoinPoint proceedingJoinPoint, MDCWrapper mdc, Instant start)
            throws Throwable {
        log.info(LogEvent.PROCESSING_START.outputValue());
        Object value = null;
        try {
            value = proceedingJoinPoint.proceed();
            mdc.put(MDCWrapper.Attr.TIME_ELAPSED_MS, String.valueOf(Duration.between(start, Instant.now()).toMillis()));
            log.info(LogEvent.PROCESSING_END.outputValue());
        } catch(Throwable t) {
            log.error(t.getMessage(), t);
            throw t;
        } finally {
            clearMDC(mdc);
        }
        return value;
    }

    /**
     * Fill common MDC entries
     * @param mdc the mdc wrapper
     * @param buildProperties the build properties
     * @param start the time
     */
    public void fillCommonMDC(MDCWrapper mdc, BuildProperties buildProperties, Instant start) {
        mdc.put(MDCWrapper.Attr.RECEPTION_DATE, MDCWrapper.FORMATTER.format(start));
        mdc.put(MDCWrapper.Attr.INTERACTION_IDENTIFIER, UUID.randomUUID().toString());
        mdc.put(MDCWrapper.Attr.APP_NAME, buildProperties != null ? buildProperties.getName() : "");
        mdc.put(MDCWrapper.Attr.APP_VERSION, buildProperties != null ? buildProperties.getVersion() : "");
        mdc.put(MDCWrapper.Attr.APP_SERVER, createAppServerAttribute());
    }

    /**
     * Clear common MDC entries
     * @param mdc the mdc wrapper
     */
    public void clearMDC(MDCWrapper mdc) {
        mdc.clear(
            MDCWrapper.Attr.APP_NAME,
            MDCWrapper.Attr.APP_VERSION,
            MDCWrapper.Attr.APP_SERVER,
            MDCWrapper.Attr.ENTRYPOINT,
            MDCWrapper.Attr.CALLING_SERVER,
            MDCWrapper.Attr.METHOD,
            MDCWrapper.Attr.PROTOCOL,
            MDCWrapper.Attr.CAUSE,
            MDCWrapper.Attr.USER_ID,
            MDCWrapper.Attr.INTERACTION_IDENTIFIER,
            MDCWrapper.Attr.TIME_ELAPSED_MS,
            MDCWrapper.Attr.RECEPTION_DATE,
            MDCWrapper.Attr.APP_COMMIT_ID,
            MDCWrapper.Attr.APP_COMMIT_DATE
        );
    }

    @Autowired(required = false)
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private String createAppServerAttribute() {
        try {
            var port = Optional.ofNullable(this.environment)
                .map(e -> ":" + e.getProperty(PROP_SERVER_PORT))
                .orElse("");
            return InetAddress.getLocalHost().getHostName() + port;
        } catch (UnknownHostException e) {
            return e.getMessage();
        }
    }

}
