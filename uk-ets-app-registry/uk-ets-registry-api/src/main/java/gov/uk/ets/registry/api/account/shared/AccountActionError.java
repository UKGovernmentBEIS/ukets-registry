package gov.uk.ets.registry.api.account.shared;

import java.io.Serializable;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;

/**
 * The Account action error.
 */
@Getter
@Builder
public class AccountActionError implements Serializable {
    /**
     * Error code for the case that the current user does not have access to the account
     */
    public static final String ACCESS_NOT_ALLOWED = "ACCESS_NOT_ALLOWED";
    public static final String ACCOUNT_STATUS_CHANGE_NOT_ALLOWED = "ACCOUNT_STATUS_CHANGE_NOT_ALLOWED";
    public static final String MULTIPLE_MONITORING_PLAN_IDS_NOT_ALLOWED = "MULTIPLE_MONITORING_PLAN_IDS_NOT_ALLOWED";
    public static final String USER_NOT_FOUND_IN_KEYCLOAK_DB = "USER_NOT_FOUND_IN_KEYCLOAK_DB";
    public static final String ACCOUNT_EXCLUDED_FROM_BILLING_CHANGE_NOT_ALLOWED = "ACCOUNT_STATUS_CHANGE_NOT_ALLOWED";
    /**
     * The error code
     */
    String code;
    /**
     * The business full identifier of account
     */
    String accountFullIdentifier;
    /**
     * The business identifier of user
     */
    String urid;
    /**
     * The error message
     */
    String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountActionError that = (AccountActionError) o;
        return code.equals(that.code) &&
                Objects.equals(accountFullIdentifier, that.accountFullIdentifier) &&
                Objects.equals(urid, that.urid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, accountFullIdentifier, urid);
    }
    

    
    public static AccountActionError build(String code, String message) {
        return AccountActionError.builder()
        					     .code(code)
        		                 .message(message)
        		                 .build();
    }
    
    public static AccountActionError build(String message) {
        return AccountActionError.builder()
        		                 .message(message)
        		                 .build();
    }
}
