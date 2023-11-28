package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

import java.util.List;

public class FirstYearOfVerifiedEmissionsCheckAllocationRule extends AbstractBusinessRule {

    public FirstYearOfVerifiedEmissionsCheckAllocationRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("First Year of Verified Emission Submission cannot be set " +
                "after the first year for which allocations have been allocated.");
    }

    @Override
    public BusinessRule.Outcome permit() {
        if (account.getCompliantEntity() == null || requestedOperatorUpdate.getFirstYear() == null) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        List<AllocationSummary> allocationEntriesForPreviousYears = allocationEntries.stream()
                .filter(ae -> ae.getAllocated() > 0 && ae.getYear() < requestedOperatorUpdate.getFirstYear()).toList();

        if (!allocationEntriesForPreviousYears.isEmpty()) {
            return forbiddenOutcome();
        }

        return Outcome.PERMITTED_OUTCOME;
    }
}
