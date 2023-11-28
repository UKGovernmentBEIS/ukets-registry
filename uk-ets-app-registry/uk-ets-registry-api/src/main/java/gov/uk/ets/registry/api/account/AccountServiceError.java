package gov.uk.ets.registry.api.account;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountServiceError {

    /**
     * Error code for the case that the current user changes the account holder id of the open accounting task and it does not exist.
     */
    public static final String ACCOUNT_HOLDER_NON_EXISTENT = "ACCOUNT_HOLDER_NON_EXISTENT";

    /**
     * Error code for the case that account does not exist.
     */
    public static final String ACCOUNT_NON_EXISTENT = "ACCOUNT_NON_EXISTENT";

    /**
     * The error code.
     */
    String code;

    /**
     * The error message
     */
    String message;
}
