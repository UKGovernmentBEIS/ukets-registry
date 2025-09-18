package gov.uk.ets.registry.api.payment;

import gov.uk.ets.registry.api.payment.service.integration.exception.PaymentIntegrationRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Log4j2
public class PaymentControllerAdvice {

    @ExceptionHandler(PaymentIntegrationRequestException.class)
    public ResponseEntity<?> handlePaymentError(PaymentIntegrationRequestException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", ex.getError().getCode(),
                "message", ex.getError().getDescription()
        ));
    }
}
