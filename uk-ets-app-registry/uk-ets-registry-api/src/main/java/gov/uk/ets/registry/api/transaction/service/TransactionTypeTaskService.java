package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.task.web.model.AllTransactionTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.Set;

public interface TransactionTypeTaskService<T extends AllTransactionTaskDetailsDTO> {

    /**
     * The assigned task types.
     *
     * @return a set of requestTypes that the service handles
     */
    Set<TransactionType> appliesFor();

    /**
     * Creates and returns the trusted account task details.
     *
     * @param taskDetailsDTO the base task dto
     * @return a task for trusted accounts dto.
     */
    T getDetails(AllTransactionTaskDetailsDTO taskDetailsDTO, Transaction transaction);


    /**
     * Implement in the respective service and then annotate them accordingly is special permissions apply.
     */
    default void checkForInvalidCompletePermissions() {
    }

    /**
     * Implement in the respective service and then annotate them accordingly is special permissions apply.
     */
    default void checkForInvalidAssignPermissions() {
    }

    /**
     * Implement in the respective service and then annotate them accordingly is special permissions apply.
     */
    default void checkForInvalidClaimantPermissions() {
    }

}



