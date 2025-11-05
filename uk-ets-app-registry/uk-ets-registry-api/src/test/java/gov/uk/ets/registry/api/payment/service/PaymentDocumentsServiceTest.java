package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.service.pdf.PaymentFileBuilder;
import gov.uk.ets.registry.api.payment.web.model.PaymentDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PaymentDocumentsServiceTest {

    @Mock
    private UploadedFilesRepository uploadedFilesRepository;

    @Mock
    private ConversionService conversionService;

    @Mock
    private PaymentFileBuilder paymentInvoiceBuilder;

    @InjectMocks
    private PaymentDocumentsService paymentDocumentsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void previewInvoicePdf_shouldReturnPdfBytes() throws Exception {
        // Arrange
        long requestId = 1L;
        long subtaskId = 10L;
        PaymentDTO dto = new PaymentDTO();
        byte[] expectedPdf = new byte[]{1, 2, 3};

        when(paymentInvoiceBuilder.createInvoicePdf(requestId, dto, subtaskId))
                .thenReturn(expectedPdf);

        // Act
        byte[] result = paymentDocumentsService.previewInvoicePdf(requestId, dto, subtaskId);

        // Assert
        assertArrayEquals(expectedPdf, result);
        verify(paymentInvoiceBuilder).createInvoicePdf(requestId, dto, subtaskId);
    }

    @Test
    void receiptPdf_shouldReturnPdfBytes() throws Exception {
        // Arrange
        Payment payment = new Payment();
        byte[] expectedPdf = new byte[]{9, 8, 7};

        when(paymentInvoiceBuilder.createReceiptPdf(payment)).thenReturn(expectedPdf);

        // Act
        byte[] result = paymentDocumentsService.previewReceiptPdf(payment);

        // Assert
        assertArrayEquals(expectedPdf, result);
        verify(paymentInvoiceBuilder).createReceiptPdf(payment);
    }

    @Test
    void generateAndPersistInvoice_shouldGeneratePdfAndSaveFile() throws Exception {
        // Arrange
        PaymentDTO dto = new PaymentDTO();
        Task task = new Task();
        task.setRequestId(42L);
        byte[] generatedPdf = new byte[]{4, 5, 6};
        String humanReadableSize = "3 KB";
        String paymentLink = "PaymentLink";

        when(paymentInvoiceBuilder.createInvoicePdf(eq(task), eq(dto), eq(paymentLink), any()))
                .thenReturn(generatedPdf);

        when(conversionService.convertByteAmountToHumanReadable(generatedPdf.length))
                .thenReturn(humanReadableSize);

        // Act
        paymentDocumentsService.generateAndPersistInvoice(dto, task, paymentLink, 1000023L);

        // Assert
        ArgumentCaptor<UploadedFile> captor = ArgumentCaptor.forClass(UploadedFile.class);
        verify(uploadedFilesRepository).save(captor.capture());

        UploadedFile uploadedFile = captor.getValue();
        assertEquals("Invoice_42", uploadedFile.getFileName());
        assertEquals(generatedPdf, uploadedFile.getFileData());
        assertEquals(humanReadableSize, uploadedFile.getFileSize());
        assertEquals(FileStatus.SUBMITTED, uploadedFile.getFileStatus());
        assertEquals(task, uploadedFile.getTask());

        verify(paymentInvoiceBuilder).createInvoicePdf(task, dto, paymentLink, 1000023L);
        verify(conversionService).convertByteAmountToHumanReadable(generatedPdf.length);
    }
}
