package gov.uk.ets.registry.api.user;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDeactivationDTO {

    private KeycloakUser userDetails;
    private EnrolmentKeyDTO enrolmentKeyDetails;
    private String deactivationComment;
    private List<String> warningMessages;

}
