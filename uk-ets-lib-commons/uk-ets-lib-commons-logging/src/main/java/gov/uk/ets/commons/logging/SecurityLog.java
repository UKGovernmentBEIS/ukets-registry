package gov.uk.ets.commons.logging;

import org.apache.logging.log4j.Logger;

/**
 * Use this to log with a special security = true entry in the MDC.
 */
public class SecurityLog {

    private SecurityLog() {

    }

    /**
     * Log helper.
     * @param logger the logger
     * @param message the message to log
     */
    public static void log(final Logger logger, String message) {
        MDCWrapper mdcWrapper = new MDCWrapper();
        mdcWrapper.put(MDCWrapper.Attr.SECURITY, "true");
        logger.warn(message);
        mdcWrapper.clear(MDCWrapper.Attr.SECURITY);
    }

    /**
     * Log helper.
     * @param logger the logger
     * @param message the message to log
     * @param cause the cause
     */
    public static void log(final Logger logger, String message, Throwable cause) {
        MDCWrapper mdcWrapper = new MDCWrapper();
        mdcWrapper.put(MDCWrapper.Attr.SECURITY, "true");
        logger.warn(message, cause);
        mdcWrapper.clear(MDCWrapper.Attr.SECURITY);
    }
}
