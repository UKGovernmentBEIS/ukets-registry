package gov.uk.ets.registry.api.transaction.domain.data;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a brief summary of the relationship between the original and the reversal transaction.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionConnectionSummary {

    private String originalIdentifier;
    private TransactionStatus originalStatus;
    private String reversalIdentifier;
    private TransactionStatus reversalStatus;
}
