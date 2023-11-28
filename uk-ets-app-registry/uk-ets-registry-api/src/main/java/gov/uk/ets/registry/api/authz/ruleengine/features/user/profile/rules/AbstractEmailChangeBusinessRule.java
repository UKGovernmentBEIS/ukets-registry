package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.EmailChangeSecurityStoreSlice;
import lombok.Getter;

/**
 * Abstract class for business rules related to email change requests.
 */
@Getter
public abstract class AbstractEmailChangeBusinessRule extends AbstractBusinessRule {
    private EmailChangeSecurityStoreSlice slice;

    public AbstractEmailChangeBusinessRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        this.slice = businessSecurityStore.getEmailChangeSecuritySlice();
    }
}
