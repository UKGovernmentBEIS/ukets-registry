package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * An AR who has been SUSPENDED from the account cannot view that account.
 */
public class ARsCanViewAccountWhenAccountAccessIsNotSuspended
    extends ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended {

    public ARsCanViewAccountWhenAccountAccessIsNotSuspended(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("An AR who has been SUSPENDED from the account cannot view that account.");
    }

}
