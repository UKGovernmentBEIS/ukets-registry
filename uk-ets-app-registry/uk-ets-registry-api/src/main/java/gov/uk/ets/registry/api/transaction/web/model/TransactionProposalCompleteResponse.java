package gov.uk.ets.registry.api.transaction.web.model;

import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * The response object to a Transaction Proposal task completion action.
 */
@Getter
@Setter
public class TransactionProposalCompleteResponse extends TaskCompleteResponse {
    /**
     * The transaction business identifier.
     */
    private String transactionIdentifier;
    /**
     * The planned transaction execution time.
     */
    private String executionTime;
    /**
     * The planned transaction execution date.
     */
    private String executionDate;

    /**
     * A builder.
     *
     * @param requestIdentifier     The request business identifier.
     * @param transactionIdentifier The transaction business identifier.
     * @param executionTime         The transaction execution time.
     * @param executionDate         The transaction execution date.
     */
    @Builder(builderMethodName = "transactionProposalCompleteResponseBuilder")
    public TransactionProposalCompleteResponse(Long requestIdentifier, String transactionIdentifier,
                                               String executionTime, String executionDate) {
        super(requestIdentifier, null);
        this.transactionIdentifier = transactionIdentifier;
        this.executionTime = executionTime;
        this.executionDate = executionDate;
    }
}
