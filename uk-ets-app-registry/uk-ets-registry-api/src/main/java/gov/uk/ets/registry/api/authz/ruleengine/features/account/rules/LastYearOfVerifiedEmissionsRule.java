package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.time.LocalDate;

/*
 * The account cannot be closed if the LYVE has not been submitted for the account
 * or if the LYVE for this account is in the future.
 * This rule is applicable only to OHA and AOHA accounts.
 * */
public class LastYearOfVerifiedEmissionsRule extends AbstractBusinessRule {

    public LastYearOfVerifiedEmissionsRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "The account cannot be closed as the last year of verified emissions has not been submitted or is in the future.");
    }

    @Override
    public Outcome permit() {
        if (account.getCompliantEntity() == null) {
             return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        if ((RegistryAccountType.OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType()) ||
            RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType())) &&
            (account.getCompliantEntity().getEndYear() == null ||
                account.getCompliantEntity().getEndYear() > LocalDate.now().getYear())) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
