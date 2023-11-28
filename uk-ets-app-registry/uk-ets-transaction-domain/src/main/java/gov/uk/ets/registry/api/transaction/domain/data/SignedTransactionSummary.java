package gov.uk.ets.registry.api.transaction.domain.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a signed transaction summary.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignedTransactionSummary extends TransactionSummary {

    private static final long serialVersionUID = -7592757131684871593L;

    private SignatureInfo signatureInfo;
}
