package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.user.KeycloakUser;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailsUpdateTaskDetailsDTO extends TaskDetailsDTO {

    private UserDetailsDTO current;
    private UserDetailsDTO changed;
    private List<KeycloakUser> userDetails;

    public UserDetailsUpdateTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
