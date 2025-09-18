package gov.uk.ets.keycloak.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jboss.logging.MDC;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.jboss.logging.Logger;

public class CustomLogger {

    private static final Logger logger = Logger.getLogger(CustomLogger.class);

    private CustomLogger() {

    }

    public static void print(Logger.Level level, String interactionIdentifier, String absolutePath, String serverResponse, String message, String error, Exception exception) {
        switch (level) {
            case WARN:
                logger.warn(logs(interactionIdentifier, absolutePath, serverResponse, message, error));
                return;
            case DEBUG:
                logger.debug(logs(interactionIdentifier, absolutePath, serverResponse, message, error));
                return;
            case ERROR:
                logger.error(logs(interactionIdentifier, absolutePath, serverResponse, message, error), exception);
                return;
            case TRACE:
                logger.trace(logs(interactionIdentifier, absolutePath, serverResponse, message, error));
                return;
            case FATAL:
                logger.fatal(logs(interactionIdentifier, absolutePath, serverResponse, message, error));
                return;
            default:
                logger.info(logs(interactionIdentifier, absolutePath, serverResponse, message, error));
        }
    }

    private static String logs(String interactionIdentifier, String absolutePath, String serverResponse, String message, String error)  {

        Instant start = Instant.now();
        MDC.put(Attr.RECEPTION_DATE.name().toLowerCase(), FORMATTER.format(start));
        MDC.put(Attr.INTERACTION_IDENTIFIER.name().toLowerCase(), Optional.ofNullable(interactionIdentifier).orElse(""));
        MDC.put(Attr.ENTRYPOINT.name().toLowerCase(), Optional.ofNullable(absolutePath).orElse(""));
        MDC.put(Attr.APP_SERVER.name().toLowerCase(), Optional.ofNullable(serverResponse).orElse(""));
        MDC.put(Attr.TYPE.name().toLowerCase(), Optional.ofNullable(message).orElse(""));
        MDC.put(Attr.CAUSE.name().toLowerCase(), Optional.ofNullable(error).orElse(""));
        try {
            return new ObjectMapper().writeValueAsString(MDC.getMap());
        } catch (JsonProcessingException e) {
            logger.error(e);
            return e.getMessage();
        }
    }

    // TODO Make public and Use the MDCWrapper.FORMATTER from common gov.uk.ets.commons.logging.
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ")
            .withZone(ZoneId.systemDefault());
            
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
}