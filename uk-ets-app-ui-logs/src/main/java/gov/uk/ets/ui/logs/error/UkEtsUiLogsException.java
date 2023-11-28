package gov.uk.ets.ui.logs.error;

public class UkEtsUiLogsException extends RuntimeException {

    public UkEtsUiLogsException(String message) {
        super(message);
    }

    public UkEtsUiLogsException(Throwable throwable) {
        super((throwable));
    }

    public UkEtsUiLogsException(String message, Throwable cause) {
        super(message, cause);
    }
}
