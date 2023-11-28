package gov.uk.ets.registry.api.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserActionErrorResponse implements Serializable {
    public static UserActionErrorResponse from(UserActionException exception) {
        UserActionErrorResponse response = new UserActionErrorResponse();
        response.setError(exception.getUserActionError());
        return response;
    }

    UserActionError error;
}
