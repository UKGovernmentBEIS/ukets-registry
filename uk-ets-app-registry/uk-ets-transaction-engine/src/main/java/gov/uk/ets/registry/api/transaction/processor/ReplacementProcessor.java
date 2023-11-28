package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("Replacement")
@Log4j2
public class ReplacementProcessor extends InternalTransferProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Transaction createInitialTransaction(TransactionSummary transactionSummary) {
        Transaction transaction = super.createInitialTransaction(transactionSummary);
        transaction.setAttributes(transactionSummary.getAttributes());
        return transaction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propose(TransactionSummary transaction) {
        super.propose(transaction);
        unitReservationService.reserveUnitsForReplacement(transaction);
        unitCreationService.createTransactionBlocksForReplacement(transaction.getIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void reject(Transaction transaction) {
        super.reject(transaction);
        unitReservationService.releaseBlocksForReplacement(transaction.getIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void terminate(Transaction transaction) {
        super.terminate(transaction);
        unitReservationService.releaseBlocksForReplacement(transaction.getIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void cancel(Transaction transaction) {
        super.cancel(transaction);
        unitReservationService.releaseBlocksForReplacement(transaction.getIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void manuallyCancel(Transaction transaction) {
        super.manuallyCancel(transaction);
        unitReservationService.releaseBlocksForReplacement(transaction.getIdentifier());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        List<UnitBlock> unitBlocks = transactionPersistenceService.getUnitBlocksForReplacement(transaction.getIdentifier());
        unitMarkingService.markUnitsForReplacement(unitBlocks);
        super.finalise(transaction);
    }
}
