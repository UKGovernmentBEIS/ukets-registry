package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import org.springframework.stereotype.Service;

@Service("check2507")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckAcquiringAccountAllowedTransferOutsideTrustedListRule extends ParentBusinessCheck {

    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary transferringAccount = getTransferringAccount(context);
        boolean transfersNotOnTALAreAllowed = transactionAccountService
                .transfersToAccountsNotOnTheTrustedListAreAllowed(transferringAccount.getIdentifier());
        if (transfersNotOnTALAreAllowed) {
            return;
        }
        AccountSummary acquiringAccount = getAcquiringAccount(context);
        boolean acquiringAccountIsTrusted =
                transactionAccountService.belongsToTrustedList(context.getTransaction(), acquiringAccount);
        if (!acquiringAccountIsTrusted) {
            addError(context, "Transfers to accounts not on the trusted account list are not allowed");
        }
    }
}
