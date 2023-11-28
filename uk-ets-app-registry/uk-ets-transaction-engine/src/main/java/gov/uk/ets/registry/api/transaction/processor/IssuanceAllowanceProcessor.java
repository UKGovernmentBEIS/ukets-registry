package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.service.AllocationPhaseCapService;
import gov.uk.ets.registry.api.allocation.service.AllocationYearCapService;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processor for Issuance of KP units.
 */
@Service("IssueAllowances")
public class IssuanceAllowanceProcessor extends IssuanceProcessor {

    /**
     * Service for yearly caps.
     */
    @Autowired
    private AllocationYearCapService allocationYearCapService;

    /**
     * Service for phase caps.
     */
    @Autowired
    private AllocationPhaseCapService allocationPhaseCapService;

    /**
     * Configuration service.
     */
    @Autowired
    private AllocationConfigurationService transactionConfigurationService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void propose(TransactionSummary transaction) {
        TransactionBlockSummary block = transaction.getBlocks().get(0);
        CommitmentPeriod period = CommitmentPeriod.CP0;
        block.setOriginalPeriod(period);
        block.setApplicablePeriod(period);
        block.setYear(transactionConfigurationService.getAllocationYear());
        super.propose(transaction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void reserveIssuanceLimit(TransactionBlockSummary block) {
        allocationYearCapService.reserveCap(block.calculateQuantity(), block.getYear());
        allocationPhaseCapService.reserveCap(block.calculateQuantity(), block.getYear());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void consumeIssuanceLimit(TransactionBlock block) {
        allocationYearCapService.consumeCap(block.getQuantity(), block.getYear());
        allocationPhaseCapService.consumeCap(block.getQuantity(), block.getYear());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void releaseIssuanceLimit(TransactionBlock block) {
        allocationYearCapService.releaseCap(block.getQuantity(), block.getYear());
        allocationPhaseCapService.releaseCap(block.getQuantity(), block.getYear());
    }

}
