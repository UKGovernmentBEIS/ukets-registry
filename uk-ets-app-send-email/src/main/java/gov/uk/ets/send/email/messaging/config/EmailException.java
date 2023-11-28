package gov.uk.ets.send.email.messaging.config;

public class EmailException extends RuntimeException {
    public EmailException(String message) {
        super(message);
    }

    public EmailException(String message, Throwable t) {
        super(message,t);
    }
}
