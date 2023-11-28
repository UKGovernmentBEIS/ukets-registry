package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.service.AllocationPhaseCapService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The quantity of allowances issued must not exceed the total cap for the current phase minus the allowances issued so far in this phase.
 */
@Service("check3500")
@AllArgsConstructor
public class CheckIssuanceLevelOfAllowances extends ParentBusinessCheck {

    /**
     * Service for transaction configuration.
     */
    private AllocationConfigurationService transactionConfigurationService;

    /**
     * Service for allocation phase caps.
     */
    private AllocationPhaseCapService allocationPhaseCapService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionSummary transaction = context.getTransaction();
        Long quantity = transaction.calculateQuantity();
        if (quantity > 0) {
            Integer year = transactionConfigurationService.getAllocationYear();
            Long remaining = allocationPhaseCapService.getRemainingCap(year);
            if (quantity > remaining) {
                addError(context, String.format("The quantity of allowances issued (%d) must not exceed the total cap for the current phase minus the allowances issued so far in this phase (%d).", quantity, remaining));
            }
        }
    }
}
