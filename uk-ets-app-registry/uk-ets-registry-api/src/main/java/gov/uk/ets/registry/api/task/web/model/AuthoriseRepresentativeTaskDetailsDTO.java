package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.user.KeycloakUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthoriseRepresentativeTaskDetailsDTO extends TaskDetailsDTO {

    private AccountInfo accountInfo;
    private AuthorisedRepresentativeDTO newUser;
    private AuthorisedRepresentativeDTO currentUser;
    private ARUpdateActionType arUpdateType;
    private AccountAccessRight arUpdateAccessRight;
    private List<KeycloakUser> userDetails;

    public AuthoriseRepresentativeTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }

}
