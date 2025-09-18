package gov.uk.ets.registry.api.payment.service.integration.exception;

import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationErrorResponseDTO;
import lombok.Getter;

@Getter
public class PaymentIntegrationRequestException extends RuntimeException {
    private final PaymentIntegrationErrorResponseDTO error;

    public PaymentIntegrationRequestException(String message, PaymentIntegrationErrorResponseDTO error) {
        super(message);
        this.error = error;
    }
}
