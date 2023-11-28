package gov.uk.ets.registry.api.account.shared;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AccountActionErrorResponse implements Serializable {
    public static AccountActionErrorResponse from(AccountActionException exception) {
        AccountActionErrorResponse response = new AccountActionErrorResponse();
        response.setError(exception.getAccountActionError());
        return response;
    }

    AccountActionError error;
}
