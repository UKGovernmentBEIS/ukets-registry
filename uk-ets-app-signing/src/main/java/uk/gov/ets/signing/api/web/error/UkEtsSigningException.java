package uk.gov.ets.signing.api.web.error;

public class UkEtsSigningException extends RuntimeException {

    public UkEtsSigningException(String message) {
        super(message);
    }

    public UkEtsSigningException(String message, Throwable t) {
        super(message, t);
    }

    public UkEtsSigningException(Throwable t) {
        super(t);
    }
}
