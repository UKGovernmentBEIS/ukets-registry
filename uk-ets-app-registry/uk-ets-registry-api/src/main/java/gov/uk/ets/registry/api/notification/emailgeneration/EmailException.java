package gov.uk.ets.registry.api.notification.emailgeneration;

public class EmailException extends RuntimeException {
    public EmailException(String message) {
        super(message);
    }

    public EmailException(String message, Throwable t) {
        super(message,t);
    }
}