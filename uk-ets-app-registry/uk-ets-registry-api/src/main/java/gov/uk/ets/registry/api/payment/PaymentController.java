package gov.uk.ets.registry.api.payment;

import static gov.uk.ets.commons.logging.RequestParamType.PAYMENT_UUID;
import static gov.uk.ets.commons.logging.RequestParamType.TASK_REQUEST_ID;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.ReadOnlyAdministratorsCannotSubmitRequest;
import gov.uk.ets.registry.api.authz.ruleengine.features.payment.rules.make.OnlyNonSucceededPaymentsCanBeSubmitted;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import gov.uk.ets.registry.api.payment.service.PaymentHistoryService;
import gov.uk.ets.registry.api.payment.service.PaymentService;
import gov.uk.ets.registry.api.payment.web.model.PaymentDTO;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistoryCriteria;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistoryDTO;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskCompleteResponse;
import gov.uk.ets.registry.api.task.shared.TaskSearchCriteria;
import gov.uk.ets.registry.api.task.web.model.TaskSearchResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Handles requests for payments.
 */
@Tag(name = "Payments Management")
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@AllArgsConstructor
public class PaymentController {

    /**
     * Service for payments.
     */
    private final PaymentService paymentService;

    /**
     * Service for payments history.
     */
    private final PaymentHistoryService paymentHistoryService;

    /**
     * Submits an payment request.
     *
     * @param parentRequestId The account full identifier.
     * @param paymentDTO     The payment DTO.
     * @return the task request identifier.
     */
    @Protected({
        ReadOnlyAdministratorsCannotSubmitRequest.class,
    })
    @PostMapping(path = "payment-request.submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> requestPayment(
        @RuleInput(RuleInputType.TASK_REQUEST_ID)
        @RequestParam @MDCParam(TASK_REQUEST_ID) Long parentRequestId,
        @RequestBody @Valid PaymentDTO paymentDTO) throws Exception {

        Long taskIdentifier = paymentService.submitPaymentRequest(parentRequestId, paymentDTO);
        return new ResponseEntity<>(taskIdentifier, HttpStatus.OK);
    }

    /**
     * Get invoice preview for a payment request.
     *
     * @param parentRequestId The account full identifier.
     * @param description     The payment DTO.
     * @param amount     The payment DTO.
     * @return the task request identifier.
     */
    @GetMapping(path = "payment-request.invoice-preview", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> previewInvoice(
        @RuleInput(RuleInputType.TASK_REQUEST_ID)
        @RequestParam @MDCParam(TASK_REQUEST_ID) Long parentRequestId,
        @RequestParam String description,
        @RequestParam String amount) throws Exception {

        byte[] pdfBytes = paymentService.generatePdf(parentRequestId,
        		PaymentDTO.builder()
        		.amount(new BigDecimal(amount))
        		.description(description)
        		.build(),
                null
                );

        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename("payment-invoice.pdf")
                .build().toString());
        headers.setContentLength(pdfBytes.length);
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Get receipt for a payment request.
     *
     * @param uuid The payment uuid.
     * @return a pdf file with relevant receipt.
     */
    @GetMapping(path = "payment-request.receipt", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getReceipt(@RequestParam String paymentUUID) throws Exception {

        byte[] pdfBytes = paymentService.previewReceiptPdf(paymentUUID);

        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename("registry-receipt-" + paymentUUID + ".pdf")
                .build().toString());
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Initiates a wallet or card payment for the given reference number.
     * 
     * <p>
     * Sets the payment method and creates a new Payment by calling the UK GOV Payment API.
     * Updates the local payment status accordingly.
     * </p>
     * 
     * @param paymentUUID the payment uuid used to identify and create the payment
     * @return the redirect URL for the UK GOV card or wallet payment page
     * 
     * @throws Exception if the payment initiation fails
     */
    @Protected({
    	OnlyNonSucceededPaymentsCanBeSubmitted.class
    })
    @PostMapping(path = "make.payment.submit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> makePayment(@RuleInput(RuleInputType.PAYMENT_UUID) @MDCParam(PAYMENT_UUID) @RequestParam String paymentUUID,@RequestParam String paymentMethod) throws Exception {

        String redirectUrl = paymentService.makePayment(paymentUUID,paymentMethod);
        
        return ResponseEntity.ok(redirectUrl);
    }

    
    @PostMapping(path = "bacs.payment.complete.or.cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> bacsPaymentCompleteOrReject(@MDCParam(PAYMENT_UUID) @RequestParam String paymentUUID,@RequestParam PaymentStatus paymentStatus) throws Exception {

    	paymentService.bacsPaymentCompleteOrReject(paymentUUID,paymentStatus);
        
        return ResponseEntity.ok(true);
    }
    
    /**
     * Retrieves the payment response for the given payment reference number.
     * 
     * <p>
     * If the payment status is already marked as finished, returns the existing data to the UI.
     * Otherwise, it fetches the current status from the UK GOV Payment API,
     * updates the local payment record accordingly, and returns the updated details.
     * </p>
     * 
     * @param referenceNumber the ID of the payment to look up
     * 
     * @return the payment details, potentially updated based on the latest integration status
     */
    @GetMapping(path = "make.payment.get.response", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentTaskCompleteResponse> paymentResponse(@MDCParam(TASK_REQUEST_ID) @RequestParam Long referenceNumber) throws Exception {

        PaymentTaskCompleteResponse response = paymentService.paymentResponse(referenceNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Searches for tasks according to the passed criteria.
     *
     * @param criteria       The {@link TaskSearchCriteria} search criteria
     * @param pageParameters The {@link PageParameters} parameters
     * @return The {@link SearchResponse} for {@link TaskSearchResult} response.
     */
    @GetMapping(path = "/payment.get.history", produces = MediaType.APPLICATION_JSON_VALUE)
    public SearchResponse<PaymentHistoryDTO> search(PaymentHistoryCriteria criteria, PageParameters pageParameters) {
        List<PaymentHistoryDTO> searchResults = paymentHistoryService.search(criteria, pageParameters);

        SearchResponse <PaymentHistoryDTO> searchResponse = new SearchResponse<>();
        searchResponse.setItems(searchResults);
        searchResponse.setPageParameters(pageParameters);
        return searchResponse;
    }
}
