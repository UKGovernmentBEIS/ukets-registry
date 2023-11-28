package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.StatusChangeSecurityStoreSlice;
import lombok.Getter;

/**
 * Abstract class for business rules related to email change requests.
 */
@Getter
public abstract class AbstractStatusChangeBusinessRule extends AbstractBusinessRule {
    private StatusChangeSecurityStoreSlice slice;

    public AbstractStatusChangeBusinessRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        this.slice = businessSecurityStore.getStatusChangeSecurityStoreSlice();
    }
}
