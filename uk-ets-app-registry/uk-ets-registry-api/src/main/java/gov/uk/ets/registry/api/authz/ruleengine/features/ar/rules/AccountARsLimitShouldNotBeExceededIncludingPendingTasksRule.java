package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

//Due to UKETS-6909 It is possible to request too many AR additions
/**
 * This rule prohibits the creation of an add ar request when the number of already
 * existing representatives plus the number of any pending add ar requests equals
 * or exceeds the maximum number of allowed ar's as defined in the property :
 * business.property.account.max.number.of.authorised.representatives.
 * 
 * @author P35036
 * @since v3.5.0
 */
public class AccountARsLimitShouldNotBeExceededIncludingPendingTasksRule extends AbstractARBusinessRule {

    private int maxNumOfARs;

    public AccountARsLimitShouldNotBeExceededIncludingPendingTasksRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        maxNumOfARs = businessSecurityStore.getMaxNumOfARs();
    }

	@Override
	public ErrorBody error() {
		long pendingAddARrequests = getSlice().getPendingARAddRequests().size();
		return ErrorBody
				.from(String.format("The account has reached the maximum number of %s Authorised Representatives",
						maxNumOfARs) + (pendingAddARrequests > 0 ? ", including pending approval tasks." : '.'));
	}

    @Override
    public Outcome permit() {

        long numberOfARs = getSlice().getAccountARs().size();
        long pendingAddARrequests = getSlice().getPendingARAddRequests().size();
        //Avoid creating a new task in case the existing number plus any pending requests
        //reach the ar limit.
        if ((numberOfARs + pendingAddARrequests) >= maxNumOfARs) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}