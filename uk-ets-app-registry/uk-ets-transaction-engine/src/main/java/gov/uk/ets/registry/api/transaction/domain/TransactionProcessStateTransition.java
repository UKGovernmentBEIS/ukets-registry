package gov.uk.ets.registry.api.transaction.domain;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents the transition of a transaction from one state to an other.
 */
@Getter
@Builder
public class TransactionProcessStateTransition {

    /**
     * The transaction
     */
    private Transaction transaction;

    /**
     * The next state of the transaction
     */
    private TransactionProcessState nextState;

    /**
     * The next status
     */
    private TransactionStatus nextStatus;
}
