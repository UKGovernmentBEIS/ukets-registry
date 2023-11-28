package gov.uk.ets.commons.s3.client;

public class UkEtsS3Exception extends RuntimeException {

    public UkEtsS3Exception(String message) {
        super(message);
    }

    public UkEtsS3Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
