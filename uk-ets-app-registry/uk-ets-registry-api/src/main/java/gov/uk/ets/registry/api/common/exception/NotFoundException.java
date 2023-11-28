package gov.uk.ets.registry.api.common.exception;

/**
 * Not found exception.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructor.
     * @param message the message to display to the user
     */
    public NotFoundException(String message) {
        super(message);
    }
}
