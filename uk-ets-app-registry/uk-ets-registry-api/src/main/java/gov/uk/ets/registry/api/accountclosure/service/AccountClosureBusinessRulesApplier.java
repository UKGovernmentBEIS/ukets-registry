package gov.uk.ets.registry.api.accountclosure.service;

import static gov.uk.ets.registry.api.authz.ruleengine.RuleInputType.ACCOUNT_FULL_ID;

import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.rules.PendingTALRequestsRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.rules.AccountWithOutstandingTasksRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.rules.AccountWithOutstandingExceptDelayedTransactionsRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.rules.LastYearOfVerifiedEmissionsRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.rules.MissingEmissionsBetweenFyveAndLyveRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.rules.NonZeroAccountBalanceRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.rules.TransferPendingWithLinkedInstallation;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanApproveTask;
import org.springframework.stereotype.Component;

/**
 * Utility class to apply the account closure business rules,
 * due to the limitation of self-invocation method calls.
 */
@Component
public class AccountClosureBusinessRulesApplier {
    /**
     * Applies the account closure business rules.
     */
    @Protected({
        FourEyesPrincipleRule.class,
        OnlySeniorRegistryAdminCanApproveTask.class,
        NonZeroAccountBalanceRule.class,
        AccountWithOutstandingTasksRule.class,
        AccountWithOutstandingExceptDelayedTransactionsRule.class,
        LastYearOfVerifiedEmissionsRule.class,
        TransferPendingWithLinkedInstallation.class,
        MissingEmissionsBetweenFyveAndLyveRule.class,
        PendingTALRequestsRule.class
    })
    public void applyAccountClosureBusinessRules(@RuleInput(ACCOUNT_FULL_ID) String accountFullIdentifier) {
        // implemented for being able to apply account closure business rules using annotations
    }
}
