package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service("check3012")
@BusinessCheckGrouping(groups = BusinessCheckGroup.UNITS)
public class CheckQuantityOfAAUToRetire extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        List<TransactionBlockSummary> blocks = context.getBlocks().stream()
                .filter(block -> CommitmentPeriod.CP2.equals(block.getApplicablePeriod())
                        && UnitType.AAU.equals(block.getType()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(blocks)) {
            return;
        }

        for (TransactionBlockSummary block : blocks) {
            Long remaining = levelService.getRemainingValue(RegistryLevelType.AAU_TO_RETIRE, UnitType.AAU, CommitmentPeriod.CP2);
            if (block.calculateQuantity() > remaining) {
                addError(context, String.format("The quantity to retire (%d) must be less or equal to the value of AAUs to Retire (%d)",
                        block.calculateQuantity(), remaining));
                return;
            }
        }
    }
}
