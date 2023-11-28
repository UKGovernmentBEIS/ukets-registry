package gov.uk.ets.registry.api.account.validation;

import java.util.List;

/**
 * Validation exception when submitting an account.
 */
public class AccountValidationException extends RuntimeException {

    /**
     * The validation errors.
     */
    private List<Violation> errors;

    /**
     * Constructor.
     * @param errors The violation errors.
     */
    public AccountValidationException(List<Violation> errors) {
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
