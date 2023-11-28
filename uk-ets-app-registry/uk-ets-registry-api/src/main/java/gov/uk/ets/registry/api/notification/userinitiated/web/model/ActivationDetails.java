package gov.uk.ets.registry.api.notification.userinitiated.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationSchedule;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class ActivationDetails {

    @NotNull(message = "The notification scheduled date is mandatory")
    private LocalDate scheduledDate;
    @NotNull(message = "The notification scheduled time is mandatory")
    private LocalTime scheduledTime;
    private LocalDate expirationDate;
    private Boolean scheduledTimeNow;

    @JsonIgnore
    public LocalDateTime getScheduledDateTime() {
        return LocalDateTime.of(this.getScheduledDate(), this.getScheduledTime());
    }

    @JsonIgnore
    public Boolean getIsScheduledTimeNow() {
        return scheduledTimeNow;
    }

    @JsonIgnore
    public abstract Optional<LocalDateTime> getExpirationDateTime();

    /**
     * Calculates the NotificationStatus depending on the scheduled date/time
     * and (optionally) on the expiration date/time
     */
    public NotificationStatus calculateStatus() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledDateTime = this.getScheduledDateTime();
        return this.getExpirationDateTime()
            .map(expirationDateTime -> {
                if (now.isBefore(scheduledDateTime)) {
                    return NotificationStatus.PENDING;
                } else if (now.isAfter(expirationDateTime)) {
                    return NotificationStatus.EXPIRED;
                } else {
                    return NotificationStatus.ACTIVE;
                }
            })
            .orElse(now.isBefore(scheduledDateTime) ? NotificationStatus.PENDING : NotificationStatus.ACTIVE);
    }

    /**
     * Calculates notifications schedule from scheduled date/time,
     * expiration date/time (optional) and recurrence days (only for compliance notifications).
     */
    public NotificationSchedule calculateSchedule(NotificationType type) {
        Integer recurrenceDays = type != NotificationType.AD_HOC ?
            ((ComplianceActivationDetails) this).getRecurrenceDays() : null;
        return NotificationSchedule.builder()
            .startDateTime(this.getScheduledDateTime())
            .endDateTime(this.getExpirationDateTime().orElse(null))
            .runEveryXDays(recurrenceDays)
            .build();
    }
}
