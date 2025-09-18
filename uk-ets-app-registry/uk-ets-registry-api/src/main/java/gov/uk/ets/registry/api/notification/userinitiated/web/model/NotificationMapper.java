package gov.uk.ets.registry.api.notification.userinitiated.web.model;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationSchedule;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.user.service.UserService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.sis.internal.util.StandardDateFormat;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final UserService userService;

    public NotificationDTO toDto(Notification notification, Integer recipients) {
        return NotificationDTO.builder()
            .type(notification.getDefinition().getType())
            .tentativeRecipients(recipients)
            .activationDetails(mapToActivationDetails(notification))
            .contentDetails(ContentDetails.builder()
                .subject(notification.getShortText())
                .content(notification.getLongText())
                .build())
            .lastUpdated(notification.getLastUpdated())
            .updatedBy(
                notification.getUpdatedBy() != null
                    ? userService.getUserByUrid(notification.getUpdatedBy()).getDisclosedName()
                    : null
            )
            .status(notification.getStatus())
            .uploadedFileId(Optional.ofNullable(notification.getUploadedFile()).map(UploadedFile::getId).orElse(null))
            .build();
    }

    public Notification toEntity(NotificationDTO request, NotificationDefinition definition,
                                 String creatorUrid, UploadedFile uploadedFile) {
        return Notification.builder()
            .definition(definition)
            .status(request.getActivationDetails().calculateStatus())
            .shortText(request.getContentDetails().getSubject())
            .longText(request.getContentDetails().getContent())
            .schedule(request.getActivationDetails().calculateSchedule(request.getType()))
            .creator(creatorUrid)
            .lastUpdated(LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC)))
            .updatedBy(creatorUrid)
            .uploadedFile(uploadedFile)
            .build();
    }

    public Notification toUpdatedEntity(Notification notification, NotificationDTO request, String urid) {
        notification.setShortText(request.getContentDetails().getSubject());
        notification.setLongText(request.getContentDetails().getContent());
        notification.setSchedule(request.getActivationDetails().calculateSchedule(request.getType()));
        notification.setStatus(request.getActivationDetails().calculateStatus());
        notification.setLastUpdated(LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC)));
        notification.setUpdatedBy(urid);
        return notification;
    }

    private ActivationDetails mapToActivationDetails(Notification notification) {
        NotificationSchedule schedule = notification.getSchedule();
        LocalDateTime startDateTime = schedule.getStartDateTime();
        LocalDateTime endDateTime = schedule.getEndDateTime();
        if (notification.getDefinition().getType().equals(NotificationType.AD_HOC)) {
            return AdHocActivationDetails.builder()
                .scheduledDate(startDateTime.toLocalDate())
                .scheduledTime(startDateTime.toLocalTime())
                .hasExpirationDateSection(endDateTime != null)
                .expirationDate(endDateTime != null ? endDateTime.toLocalDate() : null)
                .expirationTime(endDateTime != null ? endDateTime.toLocalTime() : null)
                .build();
        } else {
            return ComplianceActivationDetails.builder()
                .scheduledDate(startDateTime.toLocalDate())
                .scheduledTime(startDateTime.toLocalTime())
                .hasRecurrence(endDateTime != null)
                .expirationDate(endDateTime != null ? endDateTime.toLocalDate() : null)
                .recurrenceDays(schedule.getRunEveryXDays())
                .build();
        }
    }

}
