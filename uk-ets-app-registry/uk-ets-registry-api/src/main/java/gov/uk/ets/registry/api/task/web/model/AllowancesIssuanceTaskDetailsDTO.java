package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllowancesIssuanceTaskDetailsDTO extends AllTransactionTaskDetailsDTO {

    AcquiringAccountInfo acquiringAccount;
    List<TransactionBlockSummary> blocks;


    public AllowancesIssuanceTaskDetailsDTO(AllTransactionTaskDetailsDTO taskDetailsDTO, String reference) {

        super(taskDetailsDTO, TransactionType.IssueAllowances, reference);
    }
}
