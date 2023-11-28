package gov.uk.ets.commons.logging;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * Wraps the MDC and provides some facilities. Note that the MDC is stored in the thread local.
 */
public class MDCWrapper {

    static final DateTimeFormatter FORMATTER = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ")
        .withZone(ZoneId.systemDefault());

    private static final MDCWrapper INSTANCE = new MDCWrapper();

    /**
     * Helper so that we do no use a constructor.
     *
     * @return an MDCWrapper instance
     */
    public static MDCWrapper getOne() {
        return INSTANCE;
    }

    /**
     * Put a specific {@link Attr} in the MDC
     *
     * @param attr  the attr
     * @param value the value
     * @return the wrapper (for enabling method chaining)
     */
    public MDCWrapper put(Attr attr, String value) {
        if (!StringUtils.hasLength(MDC.get(attr.outputValue()))) {
            MDC.put(attr.outputValue(), value);
        }
        return this;
    }

    /**
     * Gets a specific {@link Attr} from the MDC
     *
     * @param key the attr
     * @return the value
     */
    public String get(Attr key) {
        return MDC.get(key.outputValue());
    }

    /**
     * Clear all MDC values
     */
    public void clear() {
        MDC.clear();
    }

    /**
     * Removes a specific {@link Attr} from the MDC
     *
     * @param key the attribute key
     */
    void clear(Attr key) {
        MDC.remove(key.outputValue());
    }

    /**
     * Clear more than 0 attrs
     *
     * @param keys the attrs
     */
    void clear(Attr... keys) {
        Arrays.stream(keys).forEach(this::clear);
    }

    /**
     * Enumeration used to have common MDC entries.
     */
    public enum Attr {
        RECEPTION_DATE,
        INTERACTION_IDENTIFIER, // x-request-id
        REQUEST_TIME, // x-request-time
        APP_NAME, // app name
        APP_VERSION, // app version
        USER_ID,
        ETS_USER_ID,
        ENTRYPOINT,
        TYPE,
        CAUSE,
        SECURITY,
        TIME_ELAPSED_MS,
        CALLING_SERVER,
        METHOD,
        PROTOCOL,
        APP_SERVER,
        APP_COMMIT_ID,
        APP_COMMIT_DATE,
        RESOURCE_ID,
        RESOURCE_TYPE,
        EXCEPTION_ERROR_CODES; // custom error codes for exceptions, comma separated (if multiple)

        private String outputValue() {
            return name().toLowerCase();
        }
    }

    /**
     * Cause of a log entry.
     */
    public enum Cause {
        MESSAGE_CONSUMPTION, // they come from Kafka
        INCOMING_REQUEST, // they come from HTTP requests
        INTERNAL_SCHEDULER; // they come from internal schedulers

        /**
         * The value that will be logged
         *
         * @return the logged value
         */
        public String outputValue() {
            return name().toLowerCase();
        }
    }

}
