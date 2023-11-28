package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import org.apache.commons.lang3.StringUtils;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

public class AuthorityUserMandatoryCommentForTaskRule extends AbstractTaskBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public AuthorityUserMandatoryCommentForTaskRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Enter a reason for applying the request.");
    }

    @Override
    public Outcome permit() {
        TaskOutcome taskOutcome = getSlice().getTaskOutcome();
        String comment = getSlice().getCompleteComment();
        if (TaskOutcome.APPROVED.equals(taskOutcome)) {
            if (!hasRole(AUTHORITY_USER)) {
                return Outcome.NOT_APPLICABLE_OUTCOME;
            } else if (hasRole(AUTHORITY_USER) && StringUtils.isEmpty(comment)) {
                return forbiddenOutcome();
            }
        }
        if (StringUtils.isEmpty(comment) && hasRole(AUTHORITY_USER)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
