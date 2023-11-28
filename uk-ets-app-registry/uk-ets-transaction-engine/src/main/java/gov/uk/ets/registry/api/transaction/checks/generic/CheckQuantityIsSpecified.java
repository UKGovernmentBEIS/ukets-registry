package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import org.springframework.stereotype.Service;

/**
 * At least one non-zero quantity must be specified.
 */
@Service("check3007")
@BusinessCheckGrouping(groups = BusinessCheckGroup.UNITS)
public class CheckQuantityIsSpecified extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionSummary transaction = context.getTransaction();

        if (!context.hasError(3008) && transaction.calculateQuantity() == 0) {
            addError(context, "At least one non-zero quantity must be specified.");
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
        return "Transaction inconsistent with Party policy (3007)";
    }

}
