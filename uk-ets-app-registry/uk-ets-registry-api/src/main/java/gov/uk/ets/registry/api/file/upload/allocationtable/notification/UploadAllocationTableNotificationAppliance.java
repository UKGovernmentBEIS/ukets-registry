package gov.uk.ets.registry.api.file.upload.allocationtable.notification;

import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(2)
@RequiredArgsConstructor
@Log4j2
public class UploadAllocationTableNotificationAppliance {

    private final GroupNotificationClient groupNotificationClient;
    private final NotificationService notificationService;
    
    /**
     * This aspect processing method is responsible for applying business logic if the underlying conditions are met.
     * <ul>
     *     <li>Method was annotated with {@link EmitsGroupNotifications}</li>
     *     <li>One of the following {@link GroupNotificationType} were
     *     used: {@link GroupNotificationType#UPLOAD_ALLOCATION_TABLE_REQUESTED}
     *     {@link GroupNotificationType#UPLOAD_ALLOCATION_TABLE_APPROVED}
     *     {@link GroupNotificationType#UPLOAD_ALLOCATION_TABLE_REJECTED}</li>
     * </ul>
     *
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
            if (GroupNotificationType.UPLOAD_ALLOCATION_TABLE_REQUESTED == groupNotificationType && result != null) {
                groupNotificationClient.emitGroupNotification(
                    generateUploadAllocationTableRequestEmailNotification(joinPoint, (Long) result));
            } else if (GroupNotificationType.UPLOAD_ALLOCATION_TABLE_COMPLETED == groupNotificationType && result != null) {
                groupNotificationClient.emitGroupNotification(
                    generateUploadAllocationTableFinalizationEmailNotification(joinPoint, (TaskCompleteResponse) result));
            }
        }
    }

    private GroupNotification generateUploadAllocationTableFinalizationEmailNotification(
        JoinPoint joinPoint, TaskCompleteResponse result) {
        TaskDetailsDTO taskDetails = (TaskDetailsDTO) joinPoint.getArgs()[0];
        TaskOutcome outcome = (TaskOutcome) joinPoint.getArgs()[1];
        if (TaskOutcome.APPROVED == outcome) {
            return new UploadAllocationTableEmailNotification(notificationService.findEmailsOfAuthorityUsers(true),
                GroupNotificationType.UPLOAD_ALLOCATION_TABLE_APPROVED,
                fromUploadedFilename(taskDetails.getFileName()),
                result.getRequestIdentifier().toString());            
        } else if (TaskOutcome.REJECTED == outcome) {
            return new UploadAllocationTableEmailNotification(notificationService.findEmailsOfAuthorityUsers(true),
                GroupNotificationType.UPLOAD_ALLOCATION_TABLE_REJECTED,
                fromUploadedFilename(taskDetails.getFileName()),
                result.getRequestIdentifier().toString());            
        }
        return null;
    }

    private GroupNotification generateUploadAllocationTableRequestEmailNotification(JoinPoint joinPoint, Long result) {
        FileHeaderDto fileHeaderDto = (FileHeaderDto) joinPoint.getArgs()[0];
        return new UploadAllocationTableEmailNotification(notificationService.findEmailsOfAuthorityUsers(true),
            GroupNotificationType.UPLOAD_ALLOCATION_TABLE_REQUESTED,
            fromUploadedFilename(fileHeaderDto.getFileName()),
            result.toString());
    }
    
    private static AllocationType fromUploadedFilename(String filename) {
        String[] fileNameArray = FilenameUtils.getBaseName(filename).split("_");
        return AllocationType.parse(fileNameArray[1]);        
    }
}
