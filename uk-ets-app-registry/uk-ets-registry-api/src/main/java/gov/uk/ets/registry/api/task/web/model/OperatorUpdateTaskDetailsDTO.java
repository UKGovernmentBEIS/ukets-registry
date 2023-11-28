package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.account.web.model.InstallationOrAircraftOperatorDTO;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperatorUpdateTaskDetailsDTO extends TaskDetailsDTO {

    private AccountInfo accountInfo;
    private InstallationOrAircraftOperatorDTO current;
    private InstallationOrAircraftOperatorDTO changed;

    public OperatorUpdateTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
