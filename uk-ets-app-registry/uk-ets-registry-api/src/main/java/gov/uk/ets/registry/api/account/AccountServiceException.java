package gov.uk.ets.registry.api.account;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings("serial")
public class AccountServiceException extends RuntimeException {

    public static AccountServiceException create(AccountServiceError error) {
        AccountServiceException
            exception = new AccountServiceException();
        exception.addError(error);
        return exception;
    }

    private final transient List<AccountServiceError> accountServiceErrors = new ArrayList<>();

    /**
     * @return The list of errors
     */
    public List<AccountServiceError> getAccountServiceErrors() {
        return accountServiceErrors;
    }

    /**
     * Adds the error to the error list.
     *
     * @param error The {@link AccountServiceError} error
     */
    public void addError(AccountServiceError error) {
        this.accountServiceErrors.add(error);
    }

    /**
     * @return the error messages of its {@link AccountServiceError} errors.
     */
    @Override
    public String getMessage() {
        return String.join("\n", accountServiceErrors.stream()
            .map(AccountServiceError::getMessage).collect(Collectors.joining()));
    }

}
