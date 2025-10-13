package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.domain.PaymentHistory;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import gov.uk.ets.registry.api.payment.repository.PaymentHistoryRepository;
import gov.uk.ets.registry.api.payment.repository.PaymentRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


/**
 * Service for auto-completion of payment tasks.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentTaskAutoCompletionService {

    private final TaskRepository taskRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserService userService;
    private final EventService eventService;
    
    /**
     * Completes the SUBMITTED_NOT_YET_APPROVED child PAYMENT_REQUEST tasks 
     * when the parent is also approved or rejected.
     * 
     * @param parentTaskId the identifier of the parent task
     */
    public void completeChildRequestedPaymentTasks(Long parentTaskId,TaskOutcome outcome) {
        String description = TaskOutcome.APPROVED.equals(outcome) ? "Task auto-completed due to parent task approval." : "Task auto-completed due to parent task rejection.";
        User currentUser = userService.getCurrentUser();
        List<Task> subTasks = taskRepository.findSubTasksParentRequestId(parentTaskId);
        subTasks.stream()
            .filter(t -> RequestStateEnum.SUBMITTED_NOT_YET_APPROVED.equals(t.getStatus()))
            .filter(childTask -> RequestType.PAYMENT_REQUEST.equals(childTask.getType()))           
            .forEach(childTask -> {

                Payment payment = paymentRepository.findByReferenceNumber(childTask.getRequestId()).orElseThrow();
                RequestStateEnum paymentOutcome = RequestStateEnum.REJECTED;
                PaymentStatus paymentStatus = PaymentStatus.CANCELLED;
                if (PaymentMethod.BACS.equals(payment.getMethod()) && PaymentStatus.SUBMITTED.equals(payment.getStatus())) {
                    paymentOutcome = RequestStateEnum.APPROVED;
                    paymentStatus = PaymentStatus.SUCCESS;     
                    payment.setAmountPaid(payment.getAmountPaid());
                }
                childTask.setCompletedDate(new Date());
                childTask.setStatus(paymentOutcome);
                childTask.setCompletedBy(currentUser);
                taskRepository.save(childTask);
                payment.setStatus(paymentStatus); 
                paymentRepository.save(payment);
                paymentHistoryRepository.save(createPaymentHistory(payment));
                //Also insert a history entry
                eventService.createAndPublishEvent(String.valueOf(childTask.getRequestId()), currentUser.getUrid(),
                    description,
                    RequestStateEnum.APPROVED.equals(paymentOutcome) ? EventType.TASK_APPROVED : EventType.TASK_REJECTED,
                    "Task completed.");
            });        
    }
    
    
    private PaymentHistory createPaymentHistory(Payment payment) {
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setAmount(payment.getAmountPaid());
        paymentHistory.setPaymentId(payment.getPaymentId());
        paymentHistory.setReferenceNumber(payment.getReferenceNumber());
        paymentHistory.setMethod(payment.getMethod());
        paymentHistory.setStatus(payment.getStatus());
        paymentHistory.setUpdated(payment.getUpdated());
        
        return paymentHistory;
    }
}
