package gov.uk.ets.registry.api.user;

import gov.uk.ets.registry.api.user.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDetails {

    private String iamIdentifier;
    private String urid;
    private UserStatus status;
    private String roleName;
    private LocalDateTime mappedOn;
}
