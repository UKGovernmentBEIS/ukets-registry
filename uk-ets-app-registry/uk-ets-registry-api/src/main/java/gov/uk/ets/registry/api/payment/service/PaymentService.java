package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.domain.PaymentHistory;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import gov.uk.ets.registry.api.payment.repository.PaymentHistoryRepository;
import gov.uk.ets.registry.api.payment.repository.PaymentRepository;
import gov.uk.ets.registry.api.payment.service.integration.UkGovPaymentIntegrationService;
import gov.uk.ets.registry.api.payment.service.integration.exception.WebLinkPaymentAlreadyCompletedException;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCreateDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCreatedResponseDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationStatusResponseDTO;
import gov.uk.ets.registry.api.payment.service.integration.utils.PaymentUrl;
import gov.uk.ets.registry.api.payment.service.mapper.PaymentIntegrationMapper;
import gov.uk.ets.registry.api.payment.web.model.PaymentDTO;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskCompleteResponse;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.service.TaskService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for payments.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PaymentService {

    private final TaskRepository taskRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserService userService;
    private final TaskService taskService;
    private final EventService eventService;
    private final TaskEventService taskEventService;
    private final PaymentDocumentsService paymentDocumentsService;
    private final PaymentTaskService paymentTaskService;
    private final UkGovPaymentIntegrationService paymentIntegrationService;
    private final PaymentIntegrationMapper paymentIntegrationMapper;
    private final PaymentUrl paymentUrl;

    /**
     * Creates a new payment task.
     * 
     * @param parentRequestId the parent task request id
     * @param paymentDTO details of the payment
     * @return the created task request identifier
     */
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.PAYMENT_REQUEST)
    public Long submitPaymentRequest(Long parentRequestId, @Valid PaymentDTO paymentDTO) throws Exception {
        Task parentTask = taskRepository.findByRequestId(parentRequestId);
        Account account = parentTask.getAccount();

        //There should be NO deadline set for the payment request.
        Task requestPaymentTask = new Task();
        requestPaymentTask.setRequestId(taskRepository.getNextRequestId());
        requestPaymentTask.setType(RequestType.PAYMENT_REQUEST);
        User currentUser = userService.getCurrentUser();
        requestPaymentTask.setInitiatedBy(currentUser);
        requestPaymentTask.setInitiatedDate(new Date());
        requestPaymentTask.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        requestPaymentTask.setAccount(account);
        requestPaymentTask.setUser(userService.getUserByUrid(paymentDTO.getRecipientUrid()));
        requestPaymentTask.setParentTask(taskRepository.findByRequestId(paymentDTO.getParentRequestId()));
        requestPaymentTask = taskRepository.save(requestPaymentTask);
        taskService.assign(List.of(requestPaymentTask.getRequestId()), 
                paymentDTO.getRecipientUrid(),
                paymentDTO.getDescription());
        
        Payment payment = new Payment();
        payment.setAmountRequested(paymentDTO.getAmount());
        payment.setDescription(paymentDTO.getDescription());
        payment.setReferenceNumber(requestPaymentTask.getRequestId());
        
        payment = paymentRepository.save(payment);

        String paymentWebLink = paymentUrl.generatePaymentWebLinkUrl(payment.getUrlSuffix());
        paymentDocumentsService.generateAndPersistInvoice(paymentDTO, requestPaymentTask, paymentWebLink, payment.getReferenceNumber());
        String action = "PAYMENT task request submitted.";
        eventService.createAndPublishEvent(payment.getUrlSuffix(), currentUser.getUrid(),
            String.format("Task requestId %s.", requestPaymentTask.getRequestId()),
            EventType.PAYMENT_TASK_REQUESTED, action);
        taskEventService.createAndPublishTaskAndAccountRequestEvent(requestPaymentTask, currentUser.getUrid());

        return requestPaymentTask.getRequestId();
    }

    /**
     * 
     * @param uuid
     * @param method
     * @return the redirect url or null in the case of BACS.
     * @throws Exception
     */
    @Transactional
    public String makePayment(String uuid,String method) throws Exception {
        Payment payment = paymentRepository.findByUrlSuffix(uuid).orElseThrow(
                () -> new NotFoundException("PAYMENT with uuid " + uuid + " not found."));
        
        PaymentMethod paymentMethod = PaymentMethod.valueOf(method);
        payment.setMethod(paymentMethod);

        if (PaymentMethod.CARD_OR_DIGITAL_WALLET.equals(paymentMethod)) {
        	
            PaymentIntegrationCreatedResponseDTO paymentResponse = paymentIntegrationService.createPayment(
                    paymentIntegrationMapper.toPaymentIntegrationCreateDTO(payment));

            paymentIntegrationMapper.setPaymentBasedOnResponse(paymentResponse, payment);
            paymentRepository.save(payment);
            
            return paymentResponse.getRedirectUrl();        	
        } else if (PaymentMethod.BACS.equals(paymentMethod)) {
            payment.setStatus(PaymentStatus.CREATED);
            paymentRepository.save(payment);
        } else if (PaymentMethod.WEBLINK.equals(paymentMethod)) {

        	Task task = taskRepository.findByRequestId(payment.getReferenceNumber());
            if (PaymentStatus.SUCCESS.equals(payment.getStatus()) || RequestStateEnum.REJECTED.equals(task.getStatus())) {
            	String erroUrl = paymentUrl.generatePaymentWebLinkErrorUrl(uuid).concat("?message=" + "The link is no longer valid.");
                throw new WebLinkPaymentAlreadyCompletedException(erroUrl);
            }
        	
        	PaymentIntegrationCreateDTO paymentRequest = paymentIntegrationMapper.createPaymentRequest(payment);
            PaymentIntegrationCreatedResponseDTO paymentResponse = paymentIntegrationService.createPayment(paymentRequest);

            paymentIntegrationMapper.setPaymentBasedOnResponse(paymentResponse, payment);

            paymentRepository.save(payment);
            return paymentResponse.getRedirectUrl();
        }
        

        return null;
    }
    
    @Transactional
    public void bacsPaymentCompleteOrReject(String uuid,PaymentStatus status) throws Exception {
        Payment payment = paymentRepository.findByUrlSuffix(uuid).orElseThrow(
                () -> new NotFoundException("PAYMENT with uuid " + uuid + " not found."));
        
        if (!payment.getMethod().equals(PaymentMethod.BACS)) {
            return;
        }
        
        payment.setStatus(status);
    	
        if (PaymentStatus.SUBMITTED.equals(status)) { 
            payment.setPaidBy(userService.getCurrentUser().getDisclosedName());
            payment.setPaidOn(LocalDate.now());
            //Amount paid should be set by the admin during task approval.
            //payment.setAmountPaid(payment.getAmountRequested());
            Task paymentTask = taskRepository.findByRequestId(payment.getReferenceNumber());
            paymentTask.setClaimedBy(paymentTask.getParentTask().getClaimedBy());
            paymentTask.setClaimedDate(new Date());
        }
        persistPaymentHistory(payment);
        paymentRepository.save(payment);
    }

    @Transactional
    public PaymentTaskCompleteResponse paymentResponse(Long referenceNumber) throws Exception {
        Payment payment = retrievePayment(referenceNumber);
        return retrievePaymentStatus(payment);
    }

    @Transactional
    public PaymentTaskCompleteResponse paymentResponse(String uuid) throws Exception {
        Payment payment = retrievePayment(uuid);
        return retrievePaymentStatus(payment);
    }

    @Transactional(readOnly = true)
    public byte[] generatePdf(Long requestId, @Valid PaymentDTO paymentDTO, Long subtaskId) throws Exception {
        return paymentDocumentsService.previewInvoicePdf(
                requestId,
                paymentDTO,
                subtaskId
        );
    }

    @Transactional(readOnly = true)
    public byte[] previewReceiptPdf(String uuid) throws Exception {
        return paymentDocumentsService.previewReceiptPdf(
                retrievePayment(uuid)
        );
    }

    private PaymentTaskCompleteResponse retrievePaymentStatus(Payment payment) throws Exception {
        if (Objects.isNull(payment.getMethod()) || Objects.isNull(payment.getStatus())) {
            throw new BusinessRuleErrorException(ErrorBody.from("PAYMENT not properly initialized."));
        }

        if (PaymentMethod.CARD_OR_DIGITAL_WALLET.equals(payment.getMethod())
                || PaymentMethod.WEBLINK.equals(payment.getMethod())) {

            if (payment.getStatus().isFinished()) {
                return paymentIntegrationMapper.paymentToPaidPaymentDTO(payment);
            } else {
                //Fetch the status from the GOV.UK pay
                PaymentIntegrationStatusResponseDTO paymentStatusResponse = paymentIntegrationService.getPaymentStatus(payment.getPaymentId());
                paymentIntegrationMapper.setPaymentBasedOnResponse(paymentStatusResponse, payment);
                
                if (PaymentStatus.SUCCESS.equals(paymentStatusResponse.getStatus())) {
                    payment.setPaidBy(paymentStatusResponse.getCardDetails().getCardholderName());
                	
                    Task paymentTask = taskRepository.findByRequestId(payment.getReferenceNumber());
                    paymentTask.setClaimedBy(paymentTask.getParentTask().getClaimedBy());
                    paymentTask.setClaimedDate(new Date());  
                    
                    paymentDocumentsService.generateAndPersistReceipt(payment, paymentTask);
                    approvePaymentTask(paymentTask);
                }

                Payment updatedPayment = paymentRepository.save(payment);

                persistPaymentHistory(updatedPayment);
                
            	PaymentTaskCompleteResponse response = paymentIntegrationMapper.paymentToPaidPaymentDTO(updatedPayment);
            	response.setTaskDetailsDTO(paymentTaskService.getDetails(response.getTaskDetailsDTO()));

                return response;
            }
        } else {
        	//Fallback in case of BACS
            return paymentIntegrationMapper.paymentToPaidPaymentDTO(payment);
        }
    }

    private Payment retrievePayment(Long referenceNumber) {
        return paymentRepository.findByReferenceNumber(referenceNumber).orElseThrow(
                () -> new NotFoundException("PAYMENT with reference number " + referenceNumber + " not found."));
    }
    
    private Payment retrievePayment(String uuid) {
        return paymentRepository.findByUrlSuffix(uuid).orElseThrow(
                () -> new NotFoundException("Payment with uuid " + uuid + " not found."));
    }

    private void persistPaymentHistory(Payment payment) {
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setAmount(payment.getAmountPaid());
        paymentHistory.setPaymentId(payment.getPaymentId());
        paymentHistory.setReferenceNumber(payment.getReferenceNumber());
        paymentHistory.setMethod(payment.getMethod());
        paymentHistory.setStatus(payment.getStatus());
        paymentHistory.setUpdated(payment.getUpdated());
        
        paymentHistoryRepository.save(paymentHistory);
    }

    private Task approvePaymentTask(Task paymentTask) {
        paymentTask.setStatus(RequestStateEnum.APPROVED);
        return taskRepository.save(paymentTask);
    }
}
