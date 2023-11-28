package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import java.io.Serializable;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

/**
 * Parent processor for allocation related transactions.
 */
public abstract class ParentAllocationProcessor extends InternalTransferProcessor {

    /**
     * Updates the allocation status of the involved account and the whole registry.
     * @param transaction The transaction.
     * @param accountIdentifier The account identifier.
     */
    @Transactional
    public void calculateAllocations(Transaction transaction, Long accountIdentifier) {
        Map<String, Serializable> attributes = getAdditionalAttributes(transaction.getAttributes());
        Integer allocationYear = (Integer) attributes.get(AllocationYear.class.getSimpleName());
        AllocationType allocationType = AllocationType.parse((String)attributes.get(AllocationType.class.getSimpleName()));
        calculationService.updateAllocationStatus(accountIdentifier, transaction.getQuantity(), allocationType,
            allocationYear, transaction.getType());
        calculationService.calculateAndUpdateRegistryStatus(allocationYear);
    }

}
