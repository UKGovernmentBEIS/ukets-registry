package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.ItlNotificationSummary;
import gov.uk.ets.registry.api.transaction.domain.data.ProposedTransactionType;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionConnectionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionTaskDetailsDTO extends AllTransactionTaskDetailsDTO {

    private ProposedTransactionType transactionType;
    private AccountInfo transferringAccount;
    private AcquiringAccountInfo acquiringAccount;
    private List<TransactionBlockSummary> transactionBlocks;
    private ItlNotificationSummary itlNotification;
    private String allocationDetails;
    private TransactionConnectionSummary transactionConnectionSummary;

    public TransactionTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO, TransactionType transactionType, String reference) {
        super(taskDetailsDTO, transactionType, reference);
    }
}
