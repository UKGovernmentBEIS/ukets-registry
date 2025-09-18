package gov.uk.ets.registry.api.authz.ruleengine.features.payment.rules.make;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.payment.PaymentSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;

public class OnlyNonSucceededPaymentsCanBeSubmitted extends AbstractBusinessRule {

    private final PaymentSecurityStoreSlice slice;
    
    public OnlyNonSucceededPaymentsCanBeSubmitted(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        slice = businessSecurityStore.getPaymentSecurityStoreSlice();
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Payment is completed.");
    }

    @Override
    public Outcome permit() {

        PaymentStatus status = slice.getPayment().getStatus();
        
        if (!PaymentStatus.SUCCESS.equals(status)) {
            return Outcome.PERMITTED_OUTCOME;
        }
        
        return forbiddenOutcome();
    }

}
