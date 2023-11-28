package gov.uk.ets.registry.api.task.web.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenTaskDetailsDTO extends TaskDetailsDTO {

    private String comment;

    private String email;

    private String firstName;

    private String lastName;

    public TokenTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }

}
