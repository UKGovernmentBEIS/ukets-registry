package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("TransferToSOPForConversionOfERU")
public class TransferToSOPForConversionOfERUProcessor extends ParentTransactionProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        unitTransferService.transferUnitsOutsideRegistry(transaction.getIdentifier());
        super.finalise(transaction);
    }
}
