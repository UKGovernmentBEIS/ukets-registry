package gov.uk.ets.registry.api.transaction.messaging;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * transaction.answer.topic response from UK Transaction log
 */
@Data
public class UKTLTransactionAnswer implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 2077774443862201958L;

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
