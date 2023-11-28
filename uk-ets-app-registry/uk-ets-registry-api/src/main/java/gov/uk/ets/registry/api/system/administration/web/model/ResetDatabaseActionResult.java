package gov.uk.ets.registry.api.system.administration.web.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResetDatabaseActionResult {

    private AccountsActionResultDTO accountsResult;
    private UsersActionResultDTO usersResult;
}
