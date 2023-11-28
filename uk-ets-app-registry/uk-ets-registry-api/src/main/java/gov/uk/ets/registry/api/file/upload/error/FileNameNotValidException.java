package gov.uk.ets.registry.api.file.upload.error;

@SuppressWarnings("serial")
public class FileNameNotValidException extends RuntimeException {

    public FileNameNotValidException(String message) {
        super(message);
    }
}
