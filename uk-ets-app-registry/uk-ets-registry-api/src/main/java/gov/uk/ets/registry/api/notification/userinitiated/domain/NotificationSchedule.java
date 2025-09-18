package gov.uk.ets.registry.api.notification.userinitiated.domain;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationSchedule {

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Column(name = "run_every_x_days")
    private Integer runEveryXDays;

}
