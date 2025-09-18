package gov.uk.ets.registry.api.itl.notice.domain;

import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeStatus;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "itl_notification_history")
public class ITLNotificationHistory {

    @Id
    @SequenceGenerator(name = "notice_log_history_id_generator", sequenceName = "itl_notification_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_log_history_id_generator")
    private Long id;

    @Column(name = "message_content")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "message_date")
    private Date messageDate;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NoticeType type;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private NoticeStatus status;

    @Column(name = "project_number")
    private String projectNUmber;

    @Column(name="unit_type")
    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    @Column(name="target_value")
    private Long targetValue;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "target_date")
    private Date targetDate;

    @Column(name="environmental_activity")
    @Enumerated(EnumType.STRING)
    private EnvironmentalActivity environmentalActivity;

    @Column(name="commitment_period")
    @Enumerated(EnumType.STRING)
    private CommitmentPeriod commitmentPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "action_due_date")
    private Date actionDueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_log_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(value= ConstraintMode.CONSTRAINT))
    private ITLNotification notification;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdDate = new Date();
}
