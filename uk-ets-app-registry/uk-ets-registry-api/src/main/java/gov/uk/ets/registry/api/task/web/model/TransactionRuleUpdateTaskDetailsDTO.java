package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRuleUpdateTaskDetailsDTO extends TaskDetailsDTO {

    private AccountInfo accountInfo;
    private TrustedAccountListRulesDTO trustedAccountListRules;

    public TransactionRuleUpdateTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
}
