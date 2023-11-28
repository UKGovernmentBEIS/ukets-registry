package gov.uk.ets.publication.api.file.upload.error;

@SuppressWarnings("serial")
public class FileTypeNotValidException extends RuntimeException {

    public FileTypeNotValidException(String message) {
        super(message);
    }
}
