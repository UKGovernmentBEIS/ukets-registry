package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperatorUpdateTaskDetailsDTO extends TaskDetailsDTO {

    private AccountInfo accountInfo;
    private OperatorDTO current;
    private OperatorDTO changed;

    public OperatorUpdateTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
