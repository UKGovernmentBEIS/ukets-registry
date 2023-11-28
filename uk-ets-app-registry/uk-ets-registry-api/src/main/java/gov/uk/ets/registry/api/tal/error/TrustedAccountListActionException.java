package gov.uk.ets.registry.api.tal.error;

import lombok.EqualsAndHashCode;

/**
 * The Trusted Account List action related runtime Exception.
 */
@EqualsAndHashCode(of = "trustedAccountListActionError", callSuper = false)
public class TrustedAccountListActionException extends RuntimeException {
    private static final long serialVersionUID = 418131946875585108L;
    private TrustedAccountListActionError trustedAccountListActionError;

    /**
     * An exception that can be thrown during a trusted account list action.
     *
     * @param error an error.
     * @return the exception.
     */
    public static TrustedAccountListActionException create(TrustedAccountListActionError error) {
        TrustedAccountListActionException
            exception = new TrustedAccountListActionException();
        exception.setError(error);
        return exception;
    }

    /**
     * Gets the action error.
     *
     * @return The list of errors.
     */
    public TrustedAccountListActionError getTrustedAccountListActionError() {
        return trustedAccountListActionError;
    }

    /**
     * Sets the error.
     *
     * @param error The {@link TrustedAccountListActionError} error.
     */
    public void setError(TrustedAccountListActionError error) {
        this.trustedAccountListActionError = error;
    }

    /**
     * Gets the exception message.
     *
     * @return the error messages of its {@link TrustedAccountListActionError} errors.
     */
    @Override
    public String getMessage() {
        return trustedAccountListActionError.message;
    }
}
