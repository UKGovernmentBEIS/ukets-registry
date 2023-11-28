package gov.uk.ets.registry.api.task.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailChangeTaskDetailsDTO extends TaskDetailsDTO {

    public EmailChangeTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
    //Properties related to the user that shall change email
    private String userLastName;
    private String userFirstName;
    private String userCurrentEmail;
    private String userNewEmail;
    private String userUrid;
}
