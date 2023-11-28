package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Units with invalid applicable Commitment Period for this transaction.
 */
@Service("check3001")
@BusinessCheckGrouping(groups = BusinessCheckGroup.UNITS)
public class CheckUnitsValidApplicableCommitmentPeriod extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {

        TransactionType transactionType = context.getTransactionType();
        CommitmentPeriod requiredApplicablePeriod = transactionType.getUnitsApplicableCommitmentPeriod();

        if (requiredApplicablePeriod == null) {
            return;
        }
        List<TransactionBlockSummary> blocks = context.getBlocks();
        boolean checkMatch = blocks.stream()
                                   .anyMatch(block -> !block.getApplicablePeriod().equals(requiredApplicablePeriod));
        if (checkMatch) {
            addError(context, "Invalid applicable Commitment Period for this transaction");
        }
    }
}
