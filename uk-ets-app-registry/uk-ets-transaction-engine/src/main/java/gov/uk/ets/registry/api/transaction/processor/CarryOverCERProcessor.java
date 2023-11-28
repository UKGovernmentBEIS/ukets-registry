package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("CarryOver_CER_ERU_FROM_AAU")
public class CarryOverCERProcessor extends CarryOverProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        unitMarkingService.markApplicablePeriod(
            transactionPersistenceService.getUnitBlocks(transaction.getIdentifier()),
            transaction.getType().getPredefinedAccountCommitmentPeriod());

        super.finalise(transaction);
    }

}
