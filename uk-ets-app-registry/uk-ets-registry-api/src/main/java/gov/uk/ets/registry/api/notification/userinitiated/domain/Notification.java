package gov.uk.ets.registry.api.notification.userinitiated.domain;


import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "notification")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Column(nullable = false)
    @Id
    @SequenceGenerator(name = "notification_id_generator", sequenceName = "notification_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_definition_id")
    @ToString.Exclude //since this is lazy we don't want it loaded when the toString() is called
    private NotificationDefinition definition;

    @Embedded
    private NotificationSchedule schedule;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String longText;

    private String shortText;

    private Long timesFired;

    /**
     * URID of the user who created the notification.
     */
    private String creator;

    private LocalDateTime lastExecutionDate;

    private LocalDateTime lastUpdated;

    /**
     * URID of user who updated the notification.
     */
    private String updatedBy;
}
