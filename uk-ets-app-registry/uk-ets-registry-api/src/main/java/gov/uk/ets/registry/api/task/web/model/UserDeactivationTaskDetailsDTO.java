package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.user.UserDeactivationDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeactivationTaskDetailsDTO extends TaskDetailsDTO {

    private UserDeactivationDTO changed;

    public UserDeactivationTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
