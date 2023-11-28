package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import org.springframework.stereotype.Service;

/**
 * The quantity of RMUs issued for each LULUCF activity type must not exceed the allowed quantity for that LULUCF activity type and Commitment Period.
 */
@Service("check3010")
public class CheckIssuanceLevelOfRemovalUnits extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionSummary transaction = context.getTransaction();

        Long quantity = transaction.calculateQuantity(UnitType.RMU);
        if (quantity > 0) {
            // This check applies only to RMUs
            final TransactionBlockSummary block = transaction.getBlocks().get(0);
            CommitmentPeriod applicablePeriod = block.getApplicablePeriod();
            EnvironmentalActivity environmentalActivity = block.getEnvironmentalActivity();
            if (environmentalActivity == null) {
                throw new IllegalArgumentException("LULUCF activity is mandatory during Issuance of RMUs.");
            }

            Long remaining = levelService.getRemainingValue(RegistryLevelType.ISSUANCE_KYOTO_LEVEL, UnitType.RMU, applicablePeriod, environmentalActivity);
            if (quantity > remaining) {
                addError(context, "The quantity of RMUs issued for each LULUCF activity type must not exceed the allowed quantity for that LULUCF activity type and Commitment Period");
            }
        }
    }

}
