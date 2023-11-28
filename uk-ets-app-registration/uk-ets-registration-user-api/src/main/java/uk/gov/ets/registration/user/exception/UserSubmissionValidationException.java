package uk.gov.ets.registration.user.exception;

import uk.gov.ets.registration.user.validation.Violation;

import java.util.List;

/**
 * Validation exception when submitting a user, at the final stage of the user registration.
 */
public class UserSubmissionValidationException extends RuntimeException {

    /**
     * The validation errors.
     */
    private List<Violation> errors;

    /**
     * Constructor.
     * @param errors The violation errors.
     */
    public UserSubmissionValidationException(List<Violation> errors) {
        super();
        this.errors = errors;
    }

    /**
     * Returns the errors.
     * @return the errors.
     */
    public List<Violation> getErrors() {
        return errors;
    }
}
