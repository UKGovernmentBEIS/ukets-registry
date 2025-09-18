package gov.uk.ets.registry.api.authz.ruleengine.features.payment;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.repository.PaymentRepository;
import jakarta.annotation.Resource;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentSecurityStoreSliceLoader {

    private BusinessSecurityStore businessSecurityStore;
    private RuleInputStore ruleInputStore;
    private final PaymentRepository paymentRepository;

    /**
     * Loads the {@link PaymentSecurityStoreSliceLoader payment store slice}.
     */
    public void load() {
        if (businessSecurityStore.getPaymentSecurityStoreSlice() != null &&
            (!ruleInputStore.containsKey(RuleInputType.PAYMENT_UUID) ||
            !ruleInputStore.containsKey(RuleInputType.REQUEST_ID))) {
            return;
        }

        PaymentSecurityStoreSlice paymentSecurityStoreSlice = new PaymentSecurityStoreSlice();
        
        
        if (ruleInputStore.containsKey(RuleInputType.PAYMENT_UUID)) {
            String uuid = (String) ruleInputStore.get(RuleInputType.PAYMENT_UUID);

            Optional<Payment> payment = paymentRepository.findByUrlSuffix(uuid);
            if (payment.isPresent()) {
                paymentSecurityStoreSlice.setPayment(payment.get());
            }
        } else if (ruleInputStore.containsKey(RuleInputType.REQUEST_ID)) {
            Long requestId = (Long) ruleInputStore.get(RuleInputType.REQUEST_ID);
            Optional<Payment> payment = paymentRepository.findByReferenceNumber(requestId);
            if (payment.isPresent()) {
                paymentSecurityStoreSlice.setPayment(payment.get());
            }
        }
        

        
        businessSecurityStore.setPaymentSecurityStoreSlice(paymentSecurityStoreSlice);
    }
    
    
    @Resource(name = "requestScopedBusinessSecurityStore")
    protected void setBusinessSecurityStore(BusinessSecurityStore businessSecurityStore) {
        this.businessSecurityStore = businessSecurityStore;
    }

    @Resource(name = "requestScopedRuleInputStore")
    protected void setRuleInputStore(RuleInputStore ruleInputStore) {
        this.ruleInputStore = ruleInputStore;
    }
}
