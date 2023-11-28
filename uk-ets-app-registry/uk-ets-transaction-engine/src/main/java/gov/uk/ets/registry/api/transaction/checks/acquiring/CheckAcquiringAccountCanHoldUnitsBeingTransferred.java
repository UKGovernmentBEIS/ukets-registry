package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.common.FullAccountIdentifierParser;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


/**
 * The acquiring account type is not allowed to hold the units being transferred.
 */
@Service("check2006")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountCanHoldUnitsBeingTransferred extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        List<TransactionBlockSummary> blocks = context.getBlocks();

        boolean unitsCheck = acquiringAccountIsNotAllowedToHoldTransactionBlocks(context, blocks);
        String identifier = context.getTransaction().getAcquiringAccountFullIdentifier();
        boolean registryCheck = !StringUtils.isEmpty(identifier) && !FullAccountIdentifierParser.getInstance(identifier).hasValidRegistryCode(context.getTransactionType());
        if (unitsCheck || registryCheck) {
            addError(context, "The acquiring account type is not allowed to hold the units being transferred");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getITLErrorNumber() {
        return 5903;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getITLErrorMessage() {
        return "Acquiring account is not eligible to receive units (2006)";
    }
}
