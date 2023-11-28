package uk.gov.ets.user.feedback.api.web.error;

public class UkEtsUserFeedbackException extends RuntimeException {

    public UkEtsUserFeedbackException(String message) {
        super(message);
    }

    public UkEtsUserFeedbackException(String message, Throwable t) {
        super(message, t);
    }

    public UkEtsUserFeedbackException(Throwable t) {
        super(t);
    }
}
