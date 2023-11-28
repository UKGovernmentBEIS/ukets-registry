package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * The acquiring account number does not exist in the registry.
 */
@Service("check2002")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountExists extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        boolean acquiringAccountExists = Optional.ofNullable(getAcquiringAccount(context)).isPresent();
        if (!acquiringAccountExists) {
            addError(context, "The acquiring account number does not exist in the registry");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getITLErrorNumber() {
        return 5902;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getITLErrorMessage() {
        return "Acquiring account does not exist (2002)";
    }
}
