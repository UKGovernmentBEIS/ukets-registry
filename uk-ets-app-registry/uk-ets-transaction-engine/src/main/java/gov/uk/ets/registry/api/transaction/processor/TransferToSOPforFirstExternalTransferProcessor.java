package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processor for Transfer to SOP for first external transfer.
 */
@Service("TransferToSOPforFirstExtTransferAAU")
public class TransferToSOPforFirstExternalTransferProcessor extends ParentTransactionProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        unitTransferService.transferUnitsOutsideRegistry(transaction.getIdentifier());
        entitlementService.updateEntitlementsOnFinalisation(transaction);
        super.finalise(transaction);
    }

}
