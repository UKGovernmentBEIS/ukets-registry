package gov.uk.ets.publication.api.error;

public class UkEtsPublicationApiClientException extends UkEtsPublicationApiException {
    public UkEtsPublicationApiClientException(String message) {
        super(message);
    }

    public UkEtsPublicationApiClientException(Throwable throwable) {
        super(throwable);
    }

    public UkEtsPublicationApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
