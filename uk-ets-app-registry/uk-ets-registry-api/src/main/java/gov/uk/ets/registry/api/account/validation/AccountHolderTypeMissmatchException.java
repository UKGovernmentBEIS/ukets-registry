package gov.uk.ets.registry.api.account.validation;

public class AccountHolderTypeMissmatchException extends Exception {
    public AccountHolderTypeMissmatchException(String message) {
        super(message);
    }
}
