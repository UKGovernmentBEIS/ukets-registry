package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * Business rule that checks that the requesting user should not be an authority user.
 */
public class AuthorityCannotBeRequestedUserRule extends AbstractARBusinessRule {

    public AuthorityCannotBeRequestedUserRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "This action cannot be performed for an authority user.");
    }
    
    @Override
    public Outcome permit() {
        if (requestedUserRoles != null && requestedUserRoles.stream().anyMatch(UserRole::isAuthority) ||
                getSlice() != null && getSlice().getCandidateUserRoles() != null && getSlice().getCandidateUserRoles().contains(AUTHORITY_USER)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
