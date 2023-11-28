package gov.uk.ets.registry.api.common.error;

public class UkEtsException extends RuntimeException {
    public UkEtsException(String message) {
        super(message);
    }

    public UkEtsException(Throwable t) {
        super(t);
    }
}
