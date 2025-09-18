package gov.uk.ets.registry.api.authz.ruleengine.features.payment;

import gov.uk.ets.registry.api.payment.domain.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentSecurityStoreSlice {

    private Payment payment;
    
}
