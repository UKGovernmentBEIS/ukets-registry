package gov.uk.ets.registry.api.transaction.checks.transferring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import org.springframework.stereotype.Service;

/**
 * Transactions from accounts with status SOME_TRANSACTIONS_RESTRICTED are not allowed.
 * Does apply even when the initiator is an administrator.
 */
@Service("check1014")
@BusinessCheckGrouping(groups = BusinessCheckGroup.TRANSFERRING_ACCOUNT)
public class CheckTransferringAccountHasNotSomeTransactionsRestrictedRegardlessOfRole extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary transferringAccount = getTransferringAccount(context);
        if (transferringAccount != null &&
            AccountStatus.SOME_TRANSACTIONS_RESTRICTED.equals(transferringAccount.getAccountStatus())) {
            addError(context, "Transactions from accounts with status SOME TRANSACTIONS RESTRICTED are not allowed.");
        }
    }
}
