package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * The account cannot be closed if emissions are missing for any year between FYVE and LYVE
 * apart from the years for which Exclusion status = TRUE.
 */
public class MissingEmissionsBetweenFyveAndLyveRule extends AbstractAccountActionBusinessRule {

    public MissingEmissionsBetweenFyveAndLyveRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("The account cannot be closed as emissions are missing for at least" +
                " one year in the reporting period of the account.");
    }

    @Override
    public Outcome permit() {
        if (account.getCompliantEntity() == null) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        if (account.getCompliantEntity().getEndYear() != null &&
            getSlice().getVerifiedEmissionsList().stream().anyMatch(vel -> vel.getReportableEmissions() == null)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
