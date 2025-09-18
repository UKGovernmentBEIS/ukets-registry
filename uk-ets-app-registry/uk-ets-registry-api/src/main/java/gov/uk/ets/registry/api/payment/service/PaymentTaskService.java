package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.repository.PaymentRepository;
import gov.uk.ets.registry.api.payment.service.integration.utils.PaymentUrl;
import gov.uk.ets.registry.api.payment.web.model.PaymentFileDTO;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskCompleteResponse;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskDetailsDTO;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskActionError;
import gov.uk.ets.registry.api.task.service.TaskServiceException;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class PaymentTaskService implements TaskTypeService<PaymentTaskDetailsDTO> {

    private final PaymentRepository paymentRepository;
    private final UploadedFilesRepository filesRepository;
    private final PaymentTaskCompleteResponseFactory paymentTaskCompleteResponseFactory;
    private final PaymentUrl paymentUrl;
    
    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.PAYMENT_REQUEST);
    }

    @Override
    public PaymentTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        PaymentTaskDetailsDTO paymentTaskDetailsDTO = new PaymentTaskDetailsDTO(taskDetailsDTO);

        Payment payment = paymentRepository.findByReferenceNumber(taskDetailsDTO.getRequestId()).orElseThrow();

        paymentTaskDetailsDTO.setAmountRequested(payment.getAmountRequested());
        paymentTaskDetailsDTO.setAmountPaid(payment.getAmountPaid());
        paymentTaskDetailsDTO.setDescription(payment.getDescription());
        paymentTaskDetailsDTO.setUuid(payment.getUrlSuffix());
        paymentTaskDetailsDTO.setPaymentLink(paymentUrl.generatePaymentWebLinkUrl(payment.getUrlSuffix()));
        paymentTaskDetailsDTO.setTaskType(RequestType.PAYMENT_REQUEST);
        paymentTaskDetailsDTO.setPaymentStatus(payment.getStatus());
        paymentTaskDetailsDTO.setPaymentMethod(payment.getMethod());

        List<UploadedFile> paymentFiles = filesRepository.findByTaskRequestId(taskDetailsDTO.getRequestId());
        
        UploadedFile invoice = paymentFiles.stream().filter(t -> t.getFileName().startsWith(PaymentDocumentsService.PaymentFilePrefix.Invoice.toString())).findFirst().orElseThrow();

        PaymentFileDTO invoiceFile = new PaymentFileDTO();
        invoiceFile.setId(invoice.getId());
        invoiceFile.setFileName(invoice.getFileName());
        invoiceFile.setFileSize(invoice.getFileSize());
        paymentTaskDetailsDTO.setInvoiceFile(invoiceFile);

        Optional<UploadedFile> receiptOptional = paymentFiles.stream().filter(t -> t.getFileName().startsWith(PaymentDocumentsService.PaymentFilePrefix.Receipt.toString())).findFirst();
        if (receiptOptional.isPresent()) {
            UploadedFile receipt = receiptOptional.get();
            PaymentFileDTO receiptFile = new PaymentFileDTO();
            receiptFile.setId(receipt.getId());
            receiptFile.setFileName(receipt.getFileName());
            receiptFile.setFileSize(receipt.getFileSize());
            paymentTaskDetailsDTO.setReceiptFile(receiptFile);        	
        }


        return paymentTaskDetailsDTO;
    }

    @Override
    public PaymentTaskCompleteResponse complete(PaymentTaskDetailsDTO taskDetailsDTO, TaskOutcome taskOutcome, String comment) {
        Payment payment = paymentRepository.findByReferenceNumber(taskDetailsDTO.getRequestId()).orElseThrow();
             
        return paymentTaskCompleteResponseFactory.create(payment);
    }
    
    @Override
    @Transactional
    public UploadedFile getRequestedTaskFile(TaskFileDownloadInfoDTO infoDTO) {
        Optional<UploadedFile> uploadedFileByFileIdOptional =
             filesRepository.findById(infoDTO.getFileId());
        if (uploadedFileByFileIdOptional.isEmpty()) {
            throw TaskServiceException.create(TaskActionError.builder().message("File Not found").build());
        }
        return uploadedFileByFileIdOptional.get();
    }

}
