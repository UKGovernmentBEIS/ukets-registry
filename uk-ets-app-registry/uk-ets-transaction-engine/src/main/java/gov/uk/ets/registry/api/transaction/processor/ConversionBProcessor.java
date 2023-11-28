package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("ConversionB")
public class ConversionBProcessor extends ConversionProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void propose(TransactionSummary transaction) {
        super.propose(transaction);
        unitMarkingService.markToERU(transactionPersistenceService.getTransactionBlocks(transaction.getIdentifier()), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        unitMarkingService.markProjectAndConvertToERU(
                transactionPersistenceService.getUnitBlocks(transaction.getIdentifier()),
                transaction.getBlocks(), false);

        super.finalise(transaction);
    }
}
