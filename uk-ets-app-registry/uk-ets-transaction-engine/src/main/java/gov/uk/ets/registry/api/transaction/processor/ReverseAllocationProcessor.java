package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ReverseAllocateAllowances")
public class ReverseAllocationProcessor extends ParentAllocationProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        super.finalise(transaction);
        calculateAllocations(transaction, transaction.getTransferringAccount().getAccountIdentifier());
    }

}
