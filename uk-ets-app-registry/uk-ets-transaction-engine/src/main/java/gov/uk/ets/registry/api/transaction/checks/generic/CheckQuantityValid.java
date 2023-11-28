package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

/**
 * The quantity must be a positive number without decimal places.
 */
@Service("check3008")
@BusinessCheckGrouping(groups = BusinessCheckGroup.UNITS)
public class CheckQuantityValid extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {

        List<TransactionBlockSummary> blocks = context.getBlocks();

        if (blocks.stream().anyMatch(block -> !Utils.isLong(block.getQuantity()) ||
                                            NumberUtils.createLong(block.getQuantity()) < 0)) {
            addError(context, "The quantity must be a positive number without decimal places");
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
        return "Transaction inconsistent with Party policy (3008)";
    }
}
