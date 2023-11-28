package gov.uk.ets.ui.logs.error;

public class UkEtsUiLogsClientException extends UkEtsUiLogsException {
    public UkEtsUiLogsClientException(String message) {
        super(message);
    }

    public UkEtsUiLogsClientException(Throwable throwable) {
        super(throwable);
    }

    public UkEtsUiLogsClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
