package gov.uk.ets.registry.api.payment.service.mapper;

import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import gov.uk.ets.registry.api.payment.service.PaymentTaskCompleteResponseFactory;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCreateDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCreatedResponseDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationStatusResponseDTO;
import gov.uk.ets.registry.api.payment.service.integration.utils.PaymentUrl;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskCompleteResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentIntegrationMapper {

    private final PaymentUrl paymentUrl;
    private final PaymentTaskCompleteResponseFactory paymentTaskCompleteResponseFactory;

    public PaymentIntegrationCreateDTO toPaymentIntegrationCreateDTO(Payment payment) {
        return PaymentIntegrationCreateDTO.builder()
                .amount(bigDecimalToPence(payment.getAmountRequested()))
                .reference("" + payment.getReferenceNumber())
                .description(payment.getDescription())
                .returnUrl(PaymentMethod.CARD_OR_DIGITAL_WALLET.equals(payment.getMethod()) ? 
                		paymentUrl.generatePaymentReturnUrl(payment.getReferenceNumber()) : 
                			paymentUrl.generatePaymentWebLinkReturnUrl(payment.getUrlSuffix()))
                .build();
    }
    
    public PaymentIntegrationCreateDTO createPaymentRequest(Payment payment) {
        return PaymentIntegrationCreateDTO.builder()
                .amount(bigDecimalToPence(payment.getAmountRequested()))
                .reference(payment.getReferenceNumber().toString())
                .description(payment.getDescription())
                .returnUrl(PaymentMethod.CARD_OR_DIGITAL_WALLET.equals(payment.getMethod()) ? 
                		paymentUrl.generatePaymentReturnUrl(payment.getReferenceNumber()) : 
                			paymentUrl.generatePaymentWebLinkReturnUrl(payment.getUrlSuffix()))
                .build();
    }

    public void setPaymentBasedOnResponse(PaymentIntegrationCreatedResponseDTO paymentResponse, Payment payment) {
        payment.setPaymentId(paymentResponse.getPaymentId());
        payment.setStatus(paymentResponse.getStatus());
    }

    public void setPaymentBasedOnResponse(PaymentIntegrationStatusResponseDTO paymentResponse, Payment payment) {
        payment.setStatus(paymentResponse.getStatus()); //Update only the status and return
        if (paymentResponse.getStatus().equals(PaymentStatus.CANCELLED) ||
            paymentResponse.getStatus().equals(PaymentStatus.FAILED)
        ) {
            return;
        }
        payment.setAmountPaid(penceToBigDecimal(paymentResponse.getAmount()));
        payment.setPaidOn(paymentResponse.getPaidOn());
    }

    public PaymentTaskCompleteResponse paymentToPaidPaymentDTO(Payment payment) {     
        return paymentTaskCompleteResponseFactory.create(payment);
    }

    private BigDecimal penceToBigDecimal(Integer amount) {
        return BigDecimal.valueOf(amount)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private Integer bigDecimalToPence(BigDecimal amount) {
        BigDecimal scaled = amount.multiply(BigDecimal.valueOf(100));
        return scaled.setScale(0, RoundingMode.HALF_UP).intValueExact();
    }
}
