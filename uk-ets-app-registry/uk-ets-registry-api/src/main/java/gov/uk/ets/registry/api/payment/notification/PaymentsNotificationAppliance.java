package gov.uk.ets.registry.api.payment.notification;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.notification.PaymentCompletedGroupNotification;
import gov.uk.ets.registry.api.notification.PaymentRequestGroupNotification;
import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import gov.uk.ets.registry.api.payment.repository.PaymentRepository;
import gov.uk.ets.registry.api.payment.web.model.PaymentDTO;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskCompleteResponse;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
public class PaymentsNotificationAppliance {

    private final GroupNotificationClient groupNotificationClient;
    private final NotificationService notificationService;

    private final PaymentRepository paymentRepository;
    private final TaskRepository taskRepository;
    
    /**
     * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
     * <ul>
     *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
     *     <li>One of the following {@link GroupNotificationType} were
     *     used:<br> {@link GroupNotificationType#PAYMENT_REQUEST}</li>
     * </ul>
     *
     * @param joinPoint                         the joint point
     * @param emitsGroupNotificationsAnnotation the annotation
     * @param result                            the result of the intercepted method
     */
    @AfterReturning(
            value = "@annotation(emitsGroupNotificationsAnnotation)",
            returning = "result")
    public void apply(JoinPoint joinPoint,
                      @NotNull EmitsGroupNotifications emitsGroupNotificationsAnnotation,
                      Object result) {

        for (GroupNotificationType groupNotificationType : emitsGroupNotificationsAnnotation.value()) {
            if (GroupNotificationType.PAYMENT_REQUEST.equals(groupNotificationType)) {
                groupNotificationClient.emitGroupNotification(
                        generatePaymentRequestNotification(groupNotificationType, joinPoint, (Long) result));
            } else if (GroupNotificationType.PAYMENT_COMPLETED.equals(groupNotificationType)) {
                        sendPaymentCompletedNotification((PaymentTaskCompleteResponse) result);
            }//Add more cases if needed
        }
    }	

    private GroupNotification generatePaymentRequestNotification(GroupNotificationType groupNotificationType,
            JoinPoint joinPoint,
            Long requestId) {

        PaymentDTO paymentDTO = (PaymentDTO) joinPoint.getArgs()[1];

        return PaymentRequestGroupNotification.builder()
            .recipients(notificationService.findEmailOfArByUserUrid(paymentDTO.getRecipientUrid(), false))
            .requestId(Objects.toString(requestId, null))
            .type(groupNotificationType)
            .paymentDescription(paymentDTO.getDescription())
            .build();
    }

    private void sendPaymentCompletedNotification(PaymentTaskCompleteResponse result) {

        paymentRepository.findByReferenceNumber(result.getReferenceNumber())
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS || p.getStatus() == PaymentStatus.SUBMITTED)
                .map(Payment::getReferenceNumber)
                .map(taskRepository::findByRequestId)
                .map(Task::getParentTask)
                .map(Task::getClaimedBy)
                .map(claimant -> PaymentCompletedGroupNotification.builder()
                        .recipients(notificationService.findUserEmail(claimant.getUrid(), true))
                        .requestId(result.getReferenceNumber())
                        .type(GroupNotificationType.PAYMENT_COMPLETED)
                        .build())
                .ifPresent(groupNotificationClient::emitGroupNotification);
    }

}
