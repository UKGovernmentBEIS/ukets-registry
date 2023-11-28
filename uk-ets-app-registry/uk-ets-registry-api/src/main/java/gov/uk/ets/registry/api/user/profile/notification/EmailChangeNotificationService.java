package gov.uk.ets.registry.api.user.profile.notification;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.profile.domain.EmailChange;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service which responsibility is to emit notifications related to the email change request.
 */
@RequiredArgsConstructor
@Service
public class EmailChangeNotificationService {
    private final GroupNotificationClient groupNotificationClient;

    /**
     * Sends two notifications to the current user according to user's email change request.
     * One notification to user's current email and one to the new email of the request.
     * @param emailChange The information of the requested change.
     */
    @Transactional
    public void emitEmailChangeNotifications(EmailChange emailChange) {
        groupNotificationClient.emitGroupNotification(EmailChangeConfirmationNotification.builder()
            .newUserEmail(emailChange.getNewEmail())
            .confirmationUrl(emailChange.getConfirmationUrl())
            .expiration(emailChange.getExpiration())
            .build());
        groupNotificationClient
            .emitGroupNotification(new EmailChangeRequestedNotification(emailChange.getOldEmail()));
    }

    /**
     * Notifies the user for the outcome of the completed email change task.
     * @param command The various info needed for constructing the notification
     */
    public void emitEmailChangeFinalizationNotification(NotifyTaskFinalizationCommand command) {
        GroupNotification notification =
            command.outcome == TaskOutcome.APPROVED ? new EmailChangeApprovedNotification(command.newEmail)
                : new EmailChangeRejectedNotification(command.oldEmail, command.comment);
        groupNotificationClient.emitGroupNotification(notification);
    }

    /**
     * The necessary info for building the approval or rejection notification.
     */
    @EqualsAndHashCode
    @ToString
    @Builder
    public static class NotifyTaskFinalizationCommand {
        private String oldEmail;
        private String newEmail;
        private TaskOutcome outcome;
        private String comment;
    }
}
