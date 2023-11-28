package uk.gov.ets.transaction.log.messaging.types;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckError;

/**
 * Represents a response sent to an incoming transaction.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAnswer {

    /**
     * The transaction identifier.
     */
    private String transactionIdentifier;

    /**
     * The transaction status code.
     */
    private int transactionStatusCode;

    /**
     * The business check errors.
     */
    private List<BusinessCheckError> errors;
}
