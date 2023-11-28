package gov.uk.ets.registry.api.authz.ruleengine.features;

import static gov.uk.ets.registry.api.user.domain.UserRole.JUNIOR_REGISTRY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.READONLY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.SENIOR_REGISTRY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.SYSTEM_ADMINISTRATOR;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class AdminsWithAccountAccessRule extends AbstractBusinessRule {

    public AdminsWithAccountAccessRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An administrator cannot interact with accounts for which he has no access right to.");
    }

    @Override
    public Outcome permit() {
        if (!hasRole(SENIOR_REGISTRY_ADMINISTRATOR, JUNIOR_REGISTRY_ADMINISTRATOR, SYSTEM_ADMINISTRATOR,
            READONLY_ADMINISTRATOR)) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        if (hasUserAccessToAccount()) {
            return Outcome.PERMITTED_OUTCOME;
        }
        return forbiddenOutcome();
    }

    private boolean hasUserAccessToAccount() {
        return accountAccesses.stream()
            .anyMatch(
                aa -> aa.getAccount().equals(account) &&
                    aa.getRight().equals(AccountAccessRight.ROLE_BASED)
            );
    }
}
