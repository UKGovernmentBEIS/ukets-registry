package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.user.KeycloakUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Transfer object for {@link gov.uk.ets.registry.api.task.domain.types.RequestType#ACCOUNT_OPENING_REQUEST}.
 */
@Getter
@Setter
public class AccountOpeningTaskDetailsDTO extends TaskDetailsDTO {

    public AccountOpeningTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }

    private AccountDTO account;
    private List<KeycloakUser> userDetails;
}
