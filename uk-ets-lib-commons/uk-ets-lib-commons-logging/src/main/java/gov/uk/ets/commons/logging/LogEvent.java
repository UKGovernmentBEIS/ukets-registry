package gov.uk.ets.commons.logging;

/**
 * Utility enumeration to log the various <>generic</> events that happen across the application.
 * Take care to not put here events that relate to a specific service like REGISTRATION_STARTED or REGISTRATION_END
 * events.
 */
public enum LogEvent {
    PROCESSING_START,
    PROCESSING_END,
    INVOCATION_START,
    INVOCATION_END,
    UNHANDLED_EXCEPTION;

    /**
     * @return the value that will be eventually logged
     */
    public String outputValue() {
        return name().toLowerCase();
    }
}
