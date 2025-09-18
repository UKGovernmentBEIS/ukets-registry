package gov.uk.ets.registry.api.itl.notice.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="itl_notification_block")
public class ITLNotificationBlock {

    @Id
    @SequenceGenerator(name = "notice_unit_identifier_id_generator", sequenceName = "itl_notification_block_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_unit_identifier_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_log_id",
                referencedColumnName = "id",
                foreignKey = @ForeignKey(value= ConstraintMode.CONSTRAINT))
    private ITLNotification notification;

    /**
     * The start serial number of the unit block.
     */
    @Column(name = "unit_serial_block_start")
    private Long unitSerialBlockStart;

    /**
     * The end serial number of the unit block.
     */
    @Column(name = "unit_serial_block_end")
    private Long unitSerialBlockEnd;

    /**
     * The origination registry code of the unit.
     */
    @Column(name = "originating_registry_code")
    private String originatingRegistryCode;

    /**
     * Date when {@link ITLNotificationBlock this unit block} for notifications is created
     */
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdDate;
}
