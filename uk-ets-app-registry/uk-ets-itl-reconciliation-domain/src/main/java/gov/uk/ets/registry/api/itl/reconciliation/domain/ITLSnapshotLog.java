package gov.uk.ets.registry.api.itl.reconciliation.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Getter
@Setter
@Table(name = "itl_snapshot_log")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITLSnapshotLog {

    @Id
    @SequenceGenerator(name = "itl_snapshot_log_id_generator", sequenceName = "itl_snapshot_log_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itl_snapshot_log_id_generator")
    private Long id;

    // TODO: @NotFound annotation fixes a weird issue appearing ONLY in integrations tests,
    //  when persisting the ITLReconciliationLog we get a javax.persistence.EntityNotFoundException. Maybe we should revise the relationshipo here, and use MapsId
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recon_id")
    private ITLReconciliationLog reconciliationLog;

    @Column(name = "snapshot_datetime")
    private Date snapshotDatetime;

    @Column(name = "snapshot_comment")
    private String snapshotComment;
}
