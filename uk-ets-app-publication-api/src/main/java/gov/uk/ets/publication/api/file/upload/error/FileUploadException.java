package gov.uk.ets.publication.api.file.upload.error;

@SuppressWarnings("serial")
public class FileUploadException extends RuntimeException {

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable t) {
        super(message,t);
    }
}
