package gov.uk.ets.registry.api.account.validation;

public class AccountNotFoundValidationException extends Exception {
    public AccountNotFoundValidationException(String message) {
        super(message);
    }
}
