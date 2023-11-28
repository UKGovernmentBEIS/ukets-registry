package gov.uk.ets.registry.api.ar.rules;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class ARUpdateBRTestHelper {

    @Getter
    @Builder
    public static class ExpectedARCommand {
        private String urid;
        private UserStatus userStatus;
        private AccountAccessState arState;
    }


    public AccountAccess buildAR(ExpectedARCommand command) {
        User user = new User();
        user.setUrid(command.urid);
        user.setState(command.userStatus);
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setUser(user);
        accountAccess.setState(command.arState);

        return accountAccess;
    }

}
