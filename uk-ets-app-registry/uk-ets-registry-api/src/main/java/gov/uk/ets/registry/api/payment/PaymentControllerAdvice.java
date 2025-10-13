package gov.uk.ets.registry.api.payment;

import gov.uk.ets.registry.api.payment.service.integration.exception.PaymentIntegrationRequestException;
import gov.uk.ets.registry.api.payment.service.integration.exception.WebLinkPaymentAlreadyCompletedException;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    
    /**
     * Handler for the case of payment via weblinks that have already been paid.
     * @param ex
     * @return
     */
    @ExceptionHandler(WebLinkPaymentAlreadyCompletedException.class)
    public ResponseEntity<?> handlePaymentError(WebLinkPaymentAlreadyCompletedException ex) {

        String redirectUrl = ex.getReturnUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectUrl);
        
        return new ResponseEntity<>(redirectUrl, headers, HttpStatus.FOUND);

    }
}
