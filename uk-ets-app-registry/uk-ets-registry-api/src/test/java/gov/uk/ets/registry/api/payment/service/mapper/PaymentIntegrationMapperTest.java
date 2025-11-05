package gov.uk.ets.registry.api.payment.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import gov.uk.ets.registry.api.payment.service.PaymentTaskCompleteResponseFactory;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationBillingAddressDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCardDetailsDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCreateDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCreatedResponseDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationStatusResponseDTO;
import gov.uk.ets.registry.api.payment.service.integration.utils.PaymentUrl;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskCompleteResponse;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskDetailsDTO;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentIntegrationMapperTest {

    private PaymentIntegrationMapper mapper;
    private PaymentUrl paymentUrl;
    private PaymentTaskCompleteResponseFactory paymentTaskCompleteResponseFactory;
    private TaskRepository taskRepository;
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
    	paymentUrl = mock(PaymentUrl.class);
    	taskRepository = mock(TaskRepository.class);
    	authorizationService = mock(AuthorizationService.class);
        paymentTaskCompleteResponseFactory = new PaymentTaskCompleteResponseFactory(taskRepository,authorizationService);
        when(paymentUrl.generatePaymentReturnUrl(123456L)).thenReturn("https://return.url/confirm?ref=123456");
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setRequestStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        
        when(taskRepository.getTaskDetails(123456L)).thenReturn(taskDetailsDTO);
        mapper = new PaymentIntegrationMapper(paymentUrl,paymentTaskCompleteResponseFactory);
    }

    @Test
    void toPaymentIntegrationCreateDTO_shouldMapCorrectly() {
        Payment payment = new Payment();
        payment.setAmountRequested(BigDecimal.valueOf(123.45));
        payment.setReferenceNumber(123456L);
        payment.setDescription("Test description");
        payment.setMethod(PaymentMethod.CARD_OR_DIGITAL_WALLET);

        PaymentIntegrationCreateDTO dto = mapper.toPaymentIntegrationCreateDTO(payment);

        assertEquals(12345, dto.getAmount());
        assertEquals("123456", dto.getReference());
        assertEquals("Test description", dto.getDescription());
        assertEquals("https://return.url/confirm?ref=123456", dto.getReturnUrl());
    }

    @Test
    void setPaymentBasedOnResponse_created_shouldSetIdAndStatus() {
        PaymentIntegrationCreatedResponseDTO response = new PaymentIntegrationCreatedResponseDTO();
        response.setPaymentId("abc123");
        response.setStatus(PaymentStatus.CREATED);

        Payment payment = new Payment();
        mapper.setPaymentBasedOnResponse(response, payment);

        assertEquals("abc123", payment.getPaymentId());
        assertEquals(PaymentStatus.CREATED, payment.getStatus());
    }

    @Test
    void setPaymentBasedOnResponse_status_cancelled_shouldOnlySetStatus() {
        PaymentIntegrationStatusResponseDTO response = new PaymentIntegrationStatusResponseDTO();
        response.setStatus(PaymentStatus.CANCELLED);

        Payment payment = new Payment();
        payment.setAmountPaid(BigDecimal.TEN); // Should be unchanged
        mapper.setPaymentBasedOnResponse(response, payment);

        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
        assertEquals(BigDecimal.TEN, payment.getAmountPaid());
    }

    @Test
    void setPaymentBasedOnResponse_status_success_shouldMapAllFields() {
        PaymentIntegrationStatusResponseDTO response = new PaymentIntegrationStatusResponseDTO();
        response.setStatus(PaymentStatus.SUCCESS);
        response.setAmount(7890); // 78.90

        PaymentIntegrationCardDetailsDTO cardDetails = new PaymentIntegrationCardDetailsDTO();
        cardDetails.setCardholderName("John Doe");
        PaymentIntegrationBillingAddressDTO address = new PaymentIntegrationBillingAddressDTO();
        address.setCountry("UK");
        address.setCity("London");
        address.setLine1("Line 1");
        address.setLine2("Line 2");
        address.setPostcode("SW1A 1AA");
        cardDetails.setBillingAddress(address);
        response.setCardDetails(cardDetails);

        response.setEmail("john@example.com");
        response.setPaidOn(LocalDate.of(2023,11,11));

        Payment payment = new Payment();
        mapper.setPaymentBasedOnResponse(response, payment);

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(BigDecimal.valueOf(78.90).setScale(2), payment.getAmountPaid());
        assertEquals(LocalDate.of(2023,11,11), payment.getPaidOn());
    }

    @Test
    void paymentToPaidPaymentDTO_shouldMapAllFields() {
        Payment payment = new Payment();
        payment.setAmountPaid(BigDecimal.valueOf(123.45));
        payment.setAmountRequested(BigDecimal.valueOf(150.00));
        payment.setDescription("Payment for invoice");
        payment.setPaidBy("Alice Smith");
        payment.setPaidOn(LocalDate.of(2023,11,11));
        payment.setReferenceNumber(123456L);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setMethod(PaymentMethod.CARD_OR_DIGITAL_WALLET);

        PaymentTaskCompleteResponse dto = mapper.paymentToPaidPaymentDTO(payment);

        assertEquals("Alice Smith", dto.getPaidBy());
        assertEquals(LocalDate.of(2023,11,11), dto.getPaidOn());
        assertEquals(123456L, dto.getReferenceNumber());

        PaymentTaskDetailsDTO details = ((PaymentTaskDetailsDTO) dto.getTaskDetailsDTO());
        assertEquals(BigDecimal.valueOf(123.45), details.getAmountPaid());
        assertEquals(BigDecimal.valueOf(150.00), details.getAmountRequested());
        assertEquals(PaymentMethod.CARD_OR_DIGITAL_WALLET, details.getPaymentMethod());
        assertEquals(PaymentStatus.SUCCESS, details.getPaymentStatus());

        assertEquals("Payment for invoice", details.getDescription());
    }
}
