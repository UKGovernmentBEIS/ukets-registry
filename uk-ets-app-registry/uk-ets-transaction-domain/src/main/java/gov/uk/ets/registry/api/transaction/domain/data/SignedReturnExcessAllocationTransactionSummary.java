package gov.uk.ets.registry.api.transaction.domain.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignedReturnExcessAllocationTransactionSummary extends ReturnExcessAllocationTransactionSummary{

    private static final long serialVersionUID = -7592757131684871594L;

    private SignatureInfo signatureInfo;

}
