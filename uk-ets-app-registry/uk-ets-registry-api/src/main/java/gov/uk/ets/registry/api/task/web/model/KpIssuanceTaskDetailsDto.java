package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KpIssuanceTaskDetailsDto extends AllTransactionTaskDetailsDTO {

    private String transactionIdentifier;
    private String transactionType;
    private int commitmentPeriod;
    private String acquiringAccount;
    private String unitType;
    private EnvironmentalActivity environmentalActivity;
    private Long initialQuantity;
    private Long consumedQuantity;
    private Long pendingQuantity;
    private Long quantity;

    public KpIssuanceTaskDetailsDto(TaskDetailsDTO taskDetailsDTO, String reference) {
        super(taskDetailsDTO, TransactionType.IssueOfAAUsAndRMUs, reference);
    }
}
