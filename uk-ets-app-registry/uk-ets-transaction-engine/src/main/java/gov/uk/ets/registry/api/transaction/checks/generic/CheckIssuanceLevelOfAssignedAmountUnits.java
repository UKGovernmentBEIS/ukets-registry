package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import org.springframework.stereotype.Service;

/**
 * The quantity of AAUs issued must not exceed allowed quantity for the Commitment Period.
 */
@Service("check3011")
public class CheckIssuanceLevelOfAssignedAmountUnits extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionSummary transaction = context.getTransaction();

        Long quantity = transaction.calculateQuantity(UnitType.AAU);
        if (quantity > 0) {
            // This check applies only to AAUs
            CommitmentPeriod applicablePeriod = transaction.getBlocks().get(0).getApplicablePeriod();
            Long remaining = levelService.getRemainingValue(RegistryLevelType.ISSUANCE_KYOTO_LEVEL, UnitType.AAU, applicablePeriod);
            if (quantity > remaining) {
                addError(context, "The quantity of AAUs issued must not exceed allowed quantity for the Commitment Period");
            }
        }
    }
}
