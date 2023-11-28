package gov.uk.ets.registry.api.transaction.web.model;

import gov.uk.ets.registry.api.transaction.domain.data.ProposedTransactionType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * A wrapper for the transaction types result array.
 */
@Getter
@Setter
@Builder
public class TransactionTypesDTO {
    String accountId;
    List<ProposedTransactionType> result;
}
