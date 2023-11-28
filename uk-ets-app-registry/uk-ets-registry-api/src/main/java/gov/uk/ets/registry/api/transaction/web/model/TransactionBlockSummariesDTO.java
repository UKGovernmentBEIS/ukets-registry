package gov.uk.ets.registry.api.transaction.web.model;

import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * A wrapper for the transaction blocks summary result array needed by the transaction proposal wizard.
 */
@Getter
@Setter
@Builder
public class TransactionBlockSummariesDTO {
    private Long accountId;
    private TransactionType transactionType;
    private List<? extends TransactionBlockSummary> result;
}
