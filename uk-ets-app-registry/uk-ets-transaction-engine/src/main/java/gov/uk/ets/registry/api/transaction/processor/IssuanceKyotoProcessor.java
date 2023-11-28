package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processor for Issuance of KP units.
 */
@Service("IssueOfAAUsAndRMUs")
public class IssuanceKyotoProcessor extends IssuanceProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void reserveIssuanceLimit(TransactionBlockSummary block) {
        levelService.reserve(block.calculateQuantity(), RegistryLevelType.ISSUANCE_KYOTO_LEVEL, block.getType(), block.getApplicablePeriod(), block.getEnvironmentalActivity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void consumeIssuanceLimit(TransactionBlock block) {
        levelService.consume(block.getQuantity(), RegistryLevelType.ISSUANCE_KYOTO_LEVEL, block.getType(), block.getApplicablePeriod(), block.getEnvironmentalActivity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void releaseIssuanceLimit(TransactionBlock block) {
        levelService.release(block.getQuantity(), RegistryLevelType.ISSUANCE_KYOTO_LEVEL, block.getType(), block.getApplicablePeriod(), block.getEnvironmentalActivity());
    }

}
