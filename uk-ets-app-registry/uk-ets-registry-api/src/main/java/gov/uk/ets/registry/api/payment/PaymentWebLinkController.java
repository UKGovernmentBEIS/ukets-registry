package gov.uk.ets.registry.api.payment;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.commons.logging.RequestParamType;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.service.PaymentService;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskCompleteResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for payments via WebLinks.
 * 
 * Note that the default class level path (api-registry) attribute is missing 
 * from the request mapping.
 */
@Tag(name = "Payment via WebLink Management")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@AllArgsConstructor
public class PaymentWebLinkController {

    /**
     * Service for payments.
     */
    private final PaymentService paymentService;
    
    /**
     * Endpoint to be used when paying via a registry made weblink.
     * 
     * @param uuid
     * @return
     * @throws Exception
     */
    @GetMapping(path = "/payment/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> makePaymentViaWeblink(@MDCParam(RequestParamType.PAYMENT_UUID) @PathVariable String uuid) throws Exception {

        String redirectUrl = paymentService.makePayment(uuid,PaymentMethod.WEBLINK.toString());
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectUrl);
        
        return new ResponseEntity<>(redirectUrl, headers, HttpStatus.FOUND);
    }

    /**
     * Endpoint to be used when paying via a registry to get
     * the status of task in after redirect back from UK GOV Payment.
     *
     * @param uuid
     * @return
     * @throws Exception
     */
    @GetMapping(path = "/payment/{uuid}/completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentTaskCompleteResponse> paidPaymentViaWeblink(@MDCParam(RequestParamType.PAYMENT_UUID) @PathVariable String uuid) throws Exception {

        PaymentTaskCompleteResponse response = paymentService.paymentResponse(uuid);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
}
