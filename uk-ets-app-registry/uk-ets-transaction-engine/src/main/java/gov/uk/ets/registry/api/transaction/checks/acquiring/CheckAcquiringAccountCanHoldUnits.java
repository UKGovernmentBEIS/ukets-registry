package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * The acquiring account type is not allowed to hold the units being issued.
 */
@Service("check2010")
public class CheckAcquiringAccountCanHoldUnits extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        List<TransactionBlockSummary> blocks = context.getBlocks();

        if (acquiringAccountIsNotAllowedToHoldTransactionBlocks(context, blocks)) {
            addError(context, "The acquiring account type is not allowed to hold the units being issued");
        }
    }

}
