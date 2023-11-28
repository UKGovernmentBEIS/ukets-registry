package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class OTPBusinessRule extends AbstractBusinessRule {

    private boolean isOtpValid;

    public OTPBusinessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        isOtpValid = businessSecurityStore.getOtpValid();
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Invalid code. Please try again.");
    }

    @Override
    public Outcome permit() {
        return isOtpValid ? Outcome.PERMITTED_OUTCOME : forbiddenOutcome();
    }
}
