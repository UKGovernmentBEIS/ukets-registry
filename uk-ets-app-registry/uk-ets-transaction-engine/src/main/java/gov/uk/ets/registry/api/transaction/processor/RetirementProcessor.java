package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("Retirement")
public class RetirementProcessor extends InternalTransferProcessor {

    @Override
    @Transactional
    public void propose(TransactionSummary transaction) {
        super.propose(transaction);
        List<TransactionBlockSummary> blocks = transaction.getBlocks().stream()
                .filter(block -> CommitmentPeriod.CP2.equals(block.getApplicablePeriod())
                        && UnitType.AAU.equals(block.getType()))
                .collect(Collectors.toList());
        long quantity = blocks.stream().map(TransactionBlockSummary::calculateQuantity).reduce(0L, Long::sum);
        levelService.reserve(quantity, RegistryLevelType.AAU_TO_RETIRE, UnitType.AAU, CommitmentPeriod.CP2, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        super.finalise(transaction);
        long quantity = retrieveQuantityAAUForCP2(transaction);
        levelService.consume(quantity, RegistryLevelType.AAU_TO_RETIRE, UnitType.AAU, CommitmentPeriod.CP2, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void reject(Transaction transaction) {
        super.reject(transaction);
        releaseAAUToRetire(transaction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void terminate(Transaction transaction) {
        super.terminate(transaction);
        releaseAAUToRetire(transaction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void cancel(Transaction transaction) {
        super.cancel(transaction);
        releaseAAUToRetire(transaction);
    }

    /**
     * Releases the AAU to retire units
     *
     * @param transaction The transaction
     */
    private void releaseAAUToRetire(Transaction transaction) {
        long quantity = retrieveQuantityAAUForCP2(transaction);
        levelService.release(quantity, RegistryLevelType.AAU_TO_RETIRE, UnitType.AAU, CommitmentPeriod.CP2, null);
    }

    /**
     * Retrieve quantity for AAU for Commitment Period 2
     * @param transaction The transaction
     * @return The quantity of AAU
     */
    private long retrieveQuantityAAUForCP2(Transaction transaction) {
        List<TransactionBlock> blocks = getTransactionBlocks(transaction.getIdentifier()).stream()
                .filter(block -> CommitmentPeriod.CP2.equals(block.getApplicablePeriod())
                        && UnitType.AAU.equals(block.getType()))
                .collect(Collectors.toList());
        return blocks.stream().map(TransactionBlock::getQuantity).reduce(0L, Long::sum);
    }
}