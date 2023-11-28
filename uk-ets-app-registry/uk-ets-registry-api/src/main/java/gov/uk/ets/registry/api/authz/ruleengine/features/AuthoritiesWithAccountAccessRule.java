package gov.uk.ets.registry.api.authz.ruleengine.features;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class AuthoritiesWithAccountAccessRule extends AbstractBusinessRule {

    public AuthoritiesWithAccountAccessRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An authority user cannot interact with accounts for which he has no access right to.");
    }

    @Override
    public Outcome permit() {
        if (!hasRole(AUTHORITY_USER)) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        if (hasUserAccessToAccountExceptRoleBased()) {
            return Outcome.PERMITTED_OUTCOME;
        }
        return forbiddenOutcome();
    }

    private boolean hasUserAccessToAccountExceptRoleBased() {
        return accountAccesses.stream()
            .anyMatch(
                aa -> aa.getAccount().equals(account) &&
                    !aa.getRight().equals(AccountAccessRight.ROLE_BASED)
            );
    }
}
