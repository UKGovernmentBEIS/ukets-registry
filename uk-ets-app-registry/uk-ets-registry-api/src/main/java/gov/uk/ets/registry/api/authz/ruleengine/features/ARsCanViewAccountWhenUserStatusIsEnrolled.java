package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * An AR user whose status is NOT ENROLLED cannot view any account.
 */
public class ARsCanViewAccountWhenUserStatusIsEnrolled extends ARsCanSubmitUpdateWhenUserStatusIsEnrolled {

    public ARsCanViewAccountWhenUserStatusIsEnrolled(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("An AR user whose status is NOT ENROLLED cannot view any account.");
    }
}
