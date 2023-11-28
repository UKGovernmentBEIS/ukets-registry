package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ConversionA")
public class ConversionAProcessor extends ConversionProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void propose(TransactionSummary transaction) {
        super.propose(transaction);
        unitMarkingService.markToERU(transactionPersistenceService.getTransactionBlocks(transaction.getIdentifier()), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        unitMarkingService.markProjectAndConvertToERU(
                transactionPersistenceService.getUnitBlocks(transaction.getIdentifier()),
                transaction.getBlocks(), true);

        super.finalise(transaction);
    }
}
