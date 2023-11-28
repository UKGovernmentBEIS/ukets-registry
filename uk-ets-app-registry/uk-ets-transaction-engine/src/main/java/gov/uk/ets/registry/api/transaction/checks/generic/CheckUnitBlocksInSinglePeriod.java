package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


/**
 * All unit blocks in transaction must be for a single applicable Commitment Period.
 */
@Service("check3003")
@BusinessCheckGrouping(groups = BusinessCheckGroup.UNITS)
public class CheckUnitBlocksInSinglePeriod extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {

        if (!context.getTransactionType().isKyoto()) {
            return;
        }

        List<TransactionBlockSummary> blocks = context.getBlocks();
        Set<CommitmentPeriod> periods = blocks.stream()
                                              .map(TransactionBlockSummary::getApplicablePeriod)
                                              .collect(Collectors.toSet());
        if (periods.size() > 1) {
            addError(context, "All unit blocks in transaction must be for a single applicable Commitment Period");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getITLErrorNumber() {
        return 5904;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getITLErrorMessage() {
        return "Transaction inconsistent with Party policy (3003)";
    }

}
