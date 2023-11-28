package gov.uk.ets.registry.api.system.administration.web.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsersActionResultDTO {

    private int usersDeleted;
    private int usersCreated;
}
