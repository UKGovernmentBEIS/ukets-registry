package gov.uk.ets.registry.api.document.reminder;

import gov.uk.ets.registry.api.document.reminder.domain.DocumentRequestReminder;
import gov.uk.ets.registry.api.document.reminder.repository.DocumentRequestReminderRepository;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.notification.DocumentRequestReminderGroupNotification;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This scheduler will periodically check for incomplete document upload tasks
 * and notify their claimants via email.
 */
@Log4j2
@Service
@AllArgsConstructor
public class DocumentRequestReminderScheduler {

    private DocumentRequestReminderRepository documentRequestReminderRepository;
    private TaskRepository taskRepository;
    private UploadedFilesRepository uploadedFilesRepository;
    private GroupNotificationClient groupNotificationClient;

    @Transactional
    @SchedulerLock(name = "documentRequestReminderSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.document.request.reminder.start}")
    public void processIncompleteDocumentUploadTasks() {
        LockAssert.assertLocked();

        log.info("document reminder scheduler has started");

        // find document upload tasks that are not completed
        List<Task> tasksList = taskRepository.findByTypeInAndStatusNotIn(
                List.of(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD, RequestType.AR_REQUESTED_DOCUMENT_UPLOAD),
                List.of(RequestStateEnum.APPROVED, RequestStateEnum.REJECTED));

        if (CollectionUtils.isEmpty(tasksList)) {
            log.info("No document upload tasks are eligible for a reminder");
        }

        tasksList.stream()
            .filter(t -> Optional.of(t).map(Task::getClaimedBy).map(User::getEmail).isPresent())
            .forEach(task -> {
            List<UploadedFile> uploadedFiles = uploadedFilesRepository.findByTaskRequestId(task.getRequestId());
            if (uploadedFiles.size() >= 1) {

                log.info("Found incomplete upload document task with request_id: {}", task.getRequestId());

                Optional<DocumentRequestReminder> requestDocumentReminderOptional =
                        documentRequestReminderRepository.findByRequestIdentifier(task.getRequestId());

                if (requestDocumentReminderOptional.isPresent()) {
                    DocumentRequestReminder documentRequestReminder = requestDocumentReminderOptional.get();
                    if (documentRequestReminder.getReminderSentAt() != null) {
                        log.info("A document request reminder email has already been sent for request_id: {}",
                                task.getRequestId().toString());
                        return;
                    }
                }

                GroupNotification groupNotification = DocumentRequestReminderGroupNotification.builder()
                        .recipients(Set.of(task.getClaimedBy().getEmail()))
                        .requestId(Long.toString(task.getRequestId()))
                        .type(GroupNotificationType.DOCUMENT_REQUEST_REMINDER)
                        .build();

                groupNotificationClient.emitGroupNotification(groupNotification);

                log.info("A document request reminder email will be sent to {} for task request_id {}",
                        task.getClaimedBy().getEmail(), task.getRequestId());

                DocumentRequestReminder documentRequestReminder = new DocumentRequestReminder();
                documentRequestReminder.setRequestIdentifier(task.getRequestId());
                documentRequestReminder.setClaimantUrid(task.getClaimedBy().getUrid());
                documentRequestReminder.setReminderSentAt(new Date());
                documentRequestReminderRepository.save(documentRequestReminder);
            }
        });
    }

}
