package gov.uk.ets.publication.api.error;

public class UkEtsPublicationApiException extends RuntimeException {

    public UkEtsPublicationApiException(String message) {
        super(message);
    }

    public UkEtsPublicationApiException(Throwable throwable) {
        super((throwable));
    }

    public UkEtsPublicationApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
