package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.service.pdf.PaymentFileBuilder;
import gov.uk.ets.registry.api.payment.web.model.PaymentDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentDocumentsService {

    private final UploadedFilesRepository uploadedFilesRepository;
    private final ConversionService conversionService;
    private final PaymentFileBuilder paymentInvoiceBuilder;
    
    public enum PaymentFilePrefix { Invoice, Receipt};

    @Transactional(readOnly = true)
    public byte[] previewInvoicePdf(Long requestId, @Valid PaymentDTO paymentDTO, Long subtaskId) throws Exception {
        return paymentInvoiceBuilder.createInvoicePdf(
                requestId,
                paymentDTO,
                subtaskId
        );
    }

    @Transactional(readOnly = true)
    public byte[] previewReceiptPdf(Payment payment) throws Exception {
        return paymentInvoiceBuilder.createReceiptPdf(
                payment
        );
    }

    public void generateAndPersistInvoice(PaymentDTO paymentDTO, Task paymentRequestSubtask, String paymentLink, Long paymentSubtaskId) throws Exception {
        byte[] invoicePdf = paymentInvoiceBuilder.createInvoicePdf(
                paymentRequestSubtask,
                paymentDTO,
                paymentLink,
                paymentSubtaskId
        );
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(PaymentFilePrefix.Invoice.toString() + "_" + paymentRequestSubtask.getRequestId());
        uploadedFile.setFileSize(conversionService.convertByteAmountToHumanReadable(invoicePdf.length));
        uploadedFile.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile.setFileData(invoicePdf);
        uploadedFile.setTask(paymentRequestSubtask);
        uploadedFile.setCreationDate(LocalDateTime.now());
        uploadedFilesRepository.save(uploadedFile);
    }

    public void generateAndPersistReceipt(Payment payment, Task paymentRequestSubtask) throws Exception {
        byte[] invoicePdf = paymentInvoiceBuilder.createReceiptPdf(payment);
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(PaymentFilePrefix.Receipt.toString() + "_" + paymentRequestSubtask.getRequestId());
        uploadedFile.setFileSize(conversionService.convertByteAmountToHumanReadable(invoicePdf.length));
        uploadedFile.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile.setFileData(invoicePdf);
        uploadedFile.setTask(paymentRequestSubtask);
        uploadedFile.setCreationDate(LocalDateTime.now());
        uploadedFilesRepository.save(uploadedFile);
    }
}
