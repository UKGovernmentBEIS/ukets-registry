package gov.uk.ets.registry.api.authz.ruleengine.features.allocation.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.allocation.AllocationSecurityStoreSlice;
import lombok.Getter;

@Getter
public abstract class AbstractAllocationBusinessRule extends AbstractBusinessRule {

    private AllocationSecurityStoreSlice slice;

    public AbstractAllocationBusinessRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        this.slice = businessSecurityStore.getAllocationSecurityStoreSlice();
    }
}
