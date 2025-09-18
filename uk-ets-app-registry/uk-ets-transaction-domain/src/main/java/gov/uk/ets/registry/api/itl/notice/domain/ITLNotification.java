package gov.uk.ets.registry.api.itl.notice.domain;

import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeStatus;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeType;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Represents a notification.
 */
@Entity
@Getter
@Setter
@Table(name = "itl_notification")
public class ITLNotification {

    @Id
    @SequenceGenerator(name = "notice_log_id_generator", sequenceName = "itl_notification_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_log_id_generator")
    private Long id;

    /**
     * The business notification identifier which is unique
     */
    @Column(name = "identifier", nullable = false)
    private Long notificationIdentifier;

    /**
     * The notification type.
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NoticeType type;

    /**
     * The current status (e.g. Open, Completed).
     */
    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private NoticeStatus status;

    /**
     * a {@link Set set} of all the {@link ITLNotificationBlock notice unit blocks} of the notification
     */
    @OneToMany(mappedBy = "notification", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private Set<ITLNotificationBlock> unitBlockIdentifiers;

    /**
     * a {@link Set set} of all the {@link ITLNotificationBlock notice unit blocks} of the notification
     */
    @OneToMany(mappedBy = "notification", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private Set<ITLNotificationHistory> noticeLogHistories;
}
