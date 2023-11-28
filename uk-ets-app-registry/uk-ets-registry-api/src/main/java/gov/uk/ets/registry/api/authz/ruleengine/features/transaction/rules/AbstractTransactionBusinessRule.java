package gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.TransactionBusinessSecurityStoreSlice;
import lombok.Getter;

/**
 * Abstract business rule for transaction proposal requests.
 */
@Getter
public abstract class AbstractTransactionBusinessRule extends AbstractBusinessRule {
    private TransactionBusinessSecurityStoreSlice slice;

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public AbstractTransactionBusinessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        slice = businessSecurityStore.getTransactionBusinessSecurityStoreSlice();
    }
}
