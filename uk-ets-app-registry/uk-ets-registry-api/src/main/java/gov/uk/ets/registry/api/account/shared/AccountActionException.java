package gov.uk.ets.registry.api.account.shared;

import lombok.EqualsAndHashCode;

/**
 * The Task(s) Bulk Action (claim or assign) relative runtime Exception.
 */
@EqualsAndHashCode(of = "accountActionError")
public class AccountActionException extends RuntimeException {

    public static AccountActionException create(AccountActionError error) {
        AccountActionException exception = new AccountActionException();
        exception.setError(error);
        return exception;
    }

    private AccountActionError accountActionError;

    /**
     * @return The list of errors
     */
    public AccountActionError getAccountActionError() {
        return accountActionError;
    }

    /**
     * Sets the error
     * @param error The {@link AccountActionError} error
     */
    public void setError(AccountActionError error) {
        this.accountActionError =  error;
    }

    /**
     * @return the error messages of its {@link AccountActionError} errors.
     */
    @Override
    public String getMessage() {
        return accountActionError.message;
    }
}
