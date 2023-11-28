package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("InternalTransfer")
public class InternalTransferProcessor extends ParentTransactionProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        unitTransferService.transferUnitsInsideRegistry(transaction.getIdentifier(), transaction.getAcquiringAccount().getAccountIdentifier());
        super.finalise(transaction);
    }
}
