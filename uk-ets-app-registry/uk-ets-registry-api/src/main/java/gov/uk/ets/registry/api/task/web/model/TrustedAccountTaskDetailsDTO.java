package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrustedAccountTaskDetailsDTO extends TaskDetailsDTO {

    private AccountInfo accountInfo;
    private List<TrustedAccountDTO> trustedAccounts;

    public TrustedAccountTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
