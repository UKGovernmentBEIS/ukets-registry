package gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import org.apache.commons.lang3.StringUtils;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

public class AuthorityUserMandatoryCommentForTransactionRule extends AbstractTransactionBusinessRule {

    public AuthorityUserMandatoryCommentForTransactionRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Enter a reason for applying the request.");
    }

    @Override
    public Outcome permit() {
        String comment = getSlice().getComment();
        if (StringUtils.isEmpty(comment) && hasRole(AUTHORITY_USER)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
