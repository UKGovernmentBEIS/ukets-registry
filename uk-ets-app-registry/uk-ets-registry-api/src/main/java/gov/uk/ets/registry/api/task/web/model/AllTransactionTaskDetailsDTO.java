package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import lombok.Getter;

@Getter
public class AllTransactionTaskDetailsDTO extends TaskDetailsDTO {
    private TransactionType trType;
    private String reference;

    public AllTransactionTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO, TransactionType transactionType, String reference) {
        super(taskDetailsDTO);
        this.trType = transactionType;
        this.reference = reference;
    }

}
