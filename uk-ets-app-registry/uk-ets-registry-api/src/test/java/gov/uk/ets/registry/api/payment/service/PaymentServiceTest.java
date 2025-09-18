package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import gov.uk.ets.registry.api.payment.repository.PaymentRepository;
import gov.uk.ets.registry.api.payment.service.integration.UkGovPaymentIntegrationService;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCreateDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCreatedResponseDTO;
import gov.uk.ets.registry.api.payment.service.integration.utils.PaymentUrl;
import gov.uk.ets.registry.api.payment.service.integration.utils.PaymentWebLinkProperties;
import gov.uk.ets.registry.api.payment.service.integration.utils.UkGovPaymentProperties;
import gov.uk.ets.registry.api.payment.service.mapper.PaymentIntegrationMapper;
import gov.uk.ets.registry.api.payment.service.pdf.PaymentFileBuilder;
import gov.uk.ets.registry.api.payment.web.model.PaymentDTO;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskCompleteResponse;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.service.TaskService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock private TaskRepository taskRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private UserService userService;
    @Mock private TaskService taskService;
    @Mock private EventService eventService;
    @Mock private TaskEventService taskEventService;
    @Mock private PaymentFileBuilder paymentInvoiceBuilder;
    @Mock private UploadedFilesRepository uploadedFilesRepository;
    @Mock private ConversionService conversionService;
    @Mock private PaymentDocumentsService paymentDocumentsService;


    @Mock private UkGovPaymentIntegrationService paymentIntegrationService;
    @Mock private PaymentIntegrationMapper paymentIntegrationMapper;
    @Mock private PaymentUrl paymentUrl;

    @Captor ArgumentCaptor<Task> taskCaptor;
    @Captor ArgumentCaptor<Payment> paymentCaptor;
    @Captor ArgumentCaptor<UploadedFile> uploadedFileCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitPaymentRequest_createsTaskAndPaymentAndInvoice() throws Exception {
        long parentId = 123L;
        long childId = 456L;
        long requestId = 999L;
        String paymentLink = "paymentLink";

        User currentUser = new User();
        currentUser.setUrid("user-urid");

        Account account = new Account();
        Task parentTask = new Task();
        parentTask.setRequestId(parentId);
        parentTask.setAccount(account);
        parentTask.setDifference("diff-json");

        Task parentAgain = new Task();
        parentAgain.setRequestId(parentId);

        PaymentDTO dto = PaymentDTO.builder()
                .amount(new BigDecimal("50.00"))
                .description("Test payment")
                .recipientUrid("recipient-urid")
                .parentRequestId(parentId)
                .build();

        when(taskRepository.findByRequestId(parentId)).thenReturn(parentTask);
        when(taskRepository.getNextRequestId()).thenReturn(requestId);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.getUserByUrid("recipient-urid")).thenReturn(new User());
        when(taskRepository.save(any())).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setRequestId(requestId);
            return t;
        });
        when(paymentRepository.save(any())).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);   // get the argument
            payment.setUrlSuffix(paymentLink); // set random suffix
            return payment;
        });
        when(paymentInvoiceBuilder.createInvoicePdf(any(Task.class), eq(dto), any()))
                .thenReturn("fake-pdf-content".getBytes());
        when(conversionService.convertByteAmountToHumanReadable(anyLong())).thenReturn("10 KB");
        doNothing().when(paymentDocumentsService).generateAndPersistInvoice(any(PaymentDTO.class), any(Task.class), any());

        when(paymentUrl.generatePaymentWebLinkUrl(any())).thenReturn("return-url");
        Long result = paymentService.submitPaymentRequest(parentId, dto);
        assertEquals(requestId, result);

        verify(taskRepository).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertEquals(RequestType.PAYMENT_REQUEST, savedTask.getType());
        assertEquals(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED, savedTask.getStatus());

        verify(paymentRepository).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();
        savedPayment.setUrlSuffix("url-suffix");
        assertEquals(dto.getAmount(), savedPayment.getAmountRequested());
        assertEquals(dto.getDescription(), savedPayment.getDescription());
        assertEquals(requestId, savedPayment.getReferenceNumber());

        verify(paymentDocumentsService).generateAndPersistInvoice(eq(dto), any(Task.class), any());
        verify(taskEventService).createAndPublishTaskAndAccountRequestEvent(savedTask, "user-urid");
        verify(taskService).assign(List.of(requestId), "recipient-urid", "Test payment");
    }




    // === 1. payWithWalletOrCard ===
    @Test
    void testPayWithWalletOrCard_success() throws Exception {
        String uuid = "abc123";
        Payment payment = new Payment();
        payment.setReferenceNumber(100L);
        payment.setAmountRequested(BigDecimal.valueOf(123.45));
        payment.setDescription("Test");

        PaymentIntegrationCreatedResponseDTO response = new PaymentIntegrationCreatedResponseDTO();
        response.setPaymentId("external-id");
        response.setStatus(PaymentStatus.CREATED);
        response.setRedirectUrl("https://redirect.url");

        Task paymentTask = new Task();
        paymentTask.setRequestId(payment.getReferenceNumber());
        
        User anAdmin = new User();
        anAdmin.setId(348L);
        Task paymentParentTask = new Task();
        paymentParentTask.setRequestId(3454L);
        paymentParentTask.setClaimedBy(anAdmin);
        paymentTask.setParentTask(paymentParentTask);
        
        when(paymentRepository.findByUrlSuffix(uuid)).thenReturn(Optional.of(payment));
        when(paymentIntegrationMapper.toPaymentIntegrationCreateDTO(payment)).thenReturn(mock(PaymentIntegrationCreateDTO.class));
        when(paymentIntegrationService.createPayment(any())).thenReturn(response);
        when(taskRepository.findByRequestId(payment.getReferenceNumber())).thenReturn(paymentTask);

        String redirectUrl = paymentService.makePayment(uuid,PaymentMethod.CARD_OR_DIGITAL_WALLET.toString());

        assertEquals("https://redirect.url", redirectUrl);
        verify(paymentRepository).save(payment);
        verify(paymentIntegrationMapper).setPaymentBasedOnResponse(response, payment);
    }

    @Test
    void testPayWithWalletOrCard_notFound() {
        String uuid = "notfound";
        when(paymentRepository.findByUrlSuffix(uuid)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            paymentService.makePayment(uuid,PaymentMethod.CARD_OR_DIGITAL_WALLET.toString());
        });

        assertTrue(ex.getMessage().contains(uuid));
    }

    // === 2. paymentResponse ===
    @Test
    void testPaymentResponse_finishedStatus_returnsMappedDTO() throws Exception {
        Payment payment = new Payment();
        payment.setMethod(PaymentMethod.CARD_OR_DIGITAL_WALLET);
        payment.setStatus(PaymentStatus.SUCCESS);

        PaymentTaskCompleteResponse response = mock(PaymentTaskCompleteResponse.class);

        when(paymentRepository.findByReferenceNumber(100L)).thenReturn(Optional.of(payment));
        when(paymentIntegrationMapper.paymentToPaidPaymentDTO(payment)).thenReturn(response);

        PaymentTaskCompleteResponse result = paymentService.paymentResponse(100L);

        assertEquals(response, result);
    }

    @Test
    void testPaymentResponse_invalidInitialization_throwsError() {
        Payment payment = new Payment(); // no method, no status
        when(paymentRepository.findByReferenceNumber(100L)).thenReturn(Optional.of(payment));

        BusinessRuleErrorException ex = assertThrows(BusinessRuleErrorException.class,
                () -> paymentService.paymentResponse(100L));

        assertTrue(ex.getErrorBody().getErrorDetails().get(0).getMessage().contains("not properly initialized"));
    }

    @Test
    void testPaymentResponse_notFound() {
        when(paymentRepository.findByReferenceNumber(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.paymentResponse(999L));
    }

    // === 3. generatePdf ===
    @Test
    void testGeneratePdf_callsPreviewInvoicePdf() throws Exception {
        PaymentDTO dto = new PaymentDTO();
        byte[] expectedPdf = new byte[]{1, 2, 3};

        when(paymentDocumentsService.previewInvoicePdf(1L, dto, 2L)).thenReturn(expectedPdf);

        byte[] result = paymentService.generatePdf(1L, dto, 2L);

        assertArrayEquals(expectedPdf, result);
    }

    // === 4. ReceiptPDF ===
    @Test
    void testReceiptPdf_returnsExpectedBytes() throws Exception {
        Payment payment = new Payment();
        byte[] expectedPdf = new byte[]{4, 5, 6};

        when(paymentRepository.findByUrlSuffix("200L")).thenReturn(Optional.of(payment));
        when(paymentDocumentsService.previewReceiptPdf(payment)).thenReturn(expectedPdf);

        byte[] result = paymentService.previewReceiptPdf("200L");

        assertArrayEquals(expectedPdf, result);
    }

    @Test
    void testReceiptPdf_notFound() {
        when(paymentRepository.findByUrlSuffix("404L")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> paymentService.previewReceiptPdf("404L"));
    }
}
