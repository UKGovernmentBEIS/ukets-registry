package gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.AccountHolderSecurityStoreSlice;
import lombok.Getter;

/**
 * Abstract business rule for AR update requests.
 */
@Getter
public abstract class AbstractAccountHolderBusinessRule extends AbstractBusinessRule {
    private AccountHolderSecurityStoreSlice slice;

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    protected AbstractAccountHolderBusinessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        slice = businessSecurityStore.getAccountHolderSecurityStoreSlice();
    }
}
