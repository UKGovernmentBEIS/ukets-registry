package gov.uk.ets.registry.api.payment.service.mapper;


import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.domain.PaymentHistory;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentHistoryMapper {

    public PaymentHistory map(Payment payment) {
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setAmount(payment.getAmountPaid());
        paymentHistory.setPaymentId(payment.getPaymentId());
        paymentHistory.setReferenceNumber(payment.getReferenceNumber());
        paymentHistory.setMethod(payment.getMethod());
        paymentHistory.setStatus(payment.getStatus());
        paymentHistory.setUpdated(payment.getUpdated());
        return paymentHistory;
    }

    public List<PaymentHistoryDTO> toDto(List<PaymentHistory> paymentHistories) {
        List<PaymentHistoryDTO> paymentHistoriesDTO = new ArrayList<>();
        paymentHistories.forEach(paymentHistory -> {
            paymentHistoriesDTO.add(toDto(paymentHistory));
        });
        return paymentHistoriesDTO;
    }

    private PaymentHistoryDTO toDto(PaymentHistory paymentHistory) {
        return PaymentHistoryDTO.builder()
                .id(paymentHistory.getId())
                .type(paymentHistory.getType())
                .status(paymentHistory.getStatus())
                .paymentId(paymentHistory.getPaymentId())
                .amount(paymentHistory.getAmount())
                .referenceNumber(paymentHistory.getReferenceNumber())
                .created(paymentHistory.getCreated())
                .updated(paymentHistory.getUpdated())
                .method(paymentHistory.getMethod())
                .build();

    }
}
