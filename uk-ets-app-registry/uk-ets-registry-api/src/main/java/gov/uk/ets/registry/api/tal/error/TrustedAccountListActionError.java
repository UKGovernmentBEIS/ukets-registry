package gov.uk.ets.registry.api.tal.error;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * The Trusted Account List action error.
 */
@Getter
@Builder
public class TrustedAccountListActionError implements Serializable {
    private static final long serialVersionUID = -5301715512920406064L;

    /**
     * Error code for the case that account cannot be added to its own trusted account list.
     */
    public static final String ACCOUNT_CANNOT_BE_ADDED_TO_ITS_OWN_TRUSTED_ACCOUNT_LIST = "An account cannot be added to its own Trusted Account List.";

    /**
     * Error code for the case that the provided host account is not found in the registry.
     */
    public static final String ACCOUNT_NOT_FOUND = "The host account does not exist in the registry.";

    /**
     * Error code for the case that the provided host account has not been initialized in the registry.
     */
    public static final String ACCOUNT_NOT_INITIALIZED = "The host account has not been initialized in the registry.";

    /**
     * Error code for the case that the provided trusted account has not been initialized in the registry.
     */
    public static final String TRUSTED_ACCOUNT_NOT_INITIALIZED =
        "The trusted account has not been initialized in the registry.";

    /**
     * Error code for the case that the User cannot update description of an account pending to be removed from the Trusted Account List.
     */
    public static final String TRUSTED_ACCOUNT_REMOVAL_TASK_DOES_NOT_ALLOW_DESCRIPTION_CHANGE =
        "User cannot update description of an account pending to be removed from the Trusted Account List.";

    /**
     * The error code.
     */
    String code;
    /**
     * The business full identifier of the host account.
     */
    String accountFullIdentifier;
    /**
     * The business identifier of user.
     */
    String urid;
    /**
     * The error message.
     */
    String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TrustedAccountListActionError
            that = (TrustedAccountListActionError) o;
        return code.equals(that.code) &&
            Objects.equals(accountFullIdentifier, that.accountFullIdentifier) &&
            Objects.equals(urid, that.urid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, accountFullIdentifier, urid);
    }
}
