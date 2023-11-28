package gov.uk.ets.registry.api.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserActionException extends RuntimeException {
    private UserActionError userActionError;

    public static UserActionException create(UserActionError error){
        UserActionException exception = new UserActionException();
        exception.setUserActionError(error);
        return exception;
    }
}
