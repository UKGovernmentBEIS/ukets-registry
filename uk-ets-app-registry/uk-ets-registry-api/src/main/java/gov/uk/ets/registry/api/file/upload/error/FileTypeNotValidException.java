package gov.uk.ets.registry.api.file.upload.error;

@SuppressWarnings("serial")
public class FileTypeNotValidException extends RuntimeException {

    public FileTypeNotValidException(String message) {
        super(message);
    }
}
