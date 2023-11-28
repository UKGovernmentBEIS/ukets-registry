package gov.uk.ets.file.upload.error;

public class ClamavException extends RuntimeException {

    private static final long serialVersionUID = 399565479217625425L;

    public ClamavException(String message) {
        super(message);
    }
}
