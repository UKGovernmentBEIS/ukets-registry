package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.AccountSecurityStoreSlice;
import lombok.Getter;


/**
 * Abstract business rule for Account related requests.
 */
@Getter
public abstract class AbstractAccountActionBusinessRule extends AbstractBusinessRule {

    private final AccountSecurityStoreSlice slice;

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    protected AbstractAccountActionBusinessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        slice = businessSecurityStore.getAccountSecurityStoreSlice();
    }
}
