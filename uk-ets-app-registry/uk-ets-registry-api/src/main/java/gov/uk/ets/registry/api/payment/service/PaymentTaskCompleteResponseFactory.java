package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskCompleteResponse;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskDetailsDTO;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class PaymentTaskCompleteResponseFactory {

    @Transactional(readOnly = true)
    public PaymentTaskCompleteResponse create(Payment payment) {
        PaymentTaskDetailsDTO details = new PaymentTaskDetailsDTO();
        details.setTaskType(RequestType.PAYMENT_REQUEST);
        details.setAmountPaid(payment.getAmountPaid());
        details.setAmountRequested(payment.getAmountRequested());
        details.setDescription(payment.getDescription());
        details.setRequestId(payment.getReferenceNumber());
        details.setPaymentStatus(payment.getStatus());
        details.setPaymentMethod(payment.getMethod());
        return PaymentTaskCompleteResponse.paymentCompleteResponseBuilder().
                paidBy(payment.getPaidBy()).
                paidOn(payment.getPaidOn()).
                requestIdentifier(payment.getReferenceNumber()).
                referenceNumber(payment.getReferenceNumber()).
                taskDetails(details).build();
    }
}
