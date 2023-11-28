package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import lombok.Getter;

/**
 * Abstract business rule for AR update requests.
 */
@Getter
public abstract class AbstractARBusinessRule extends AbstractBusinessRule {
    private ARBusinessSecurityStoreSlice slice;

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public AbstractARBusinessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        slice = businessSecurityStore.getArUpdateStoreSlice();
    }
}
