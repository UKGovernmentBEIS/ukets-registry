package gov.uk.ets.registry.api.itl.reconciliation.domain;

import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationStatus;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "itl_reconciliation_status_history")
public class ITLReconciliationStatusHistory {

    @Id
    @SequenceGenerator(name = "itl_reconciliation_status_history_id_generator", sequenceName = "itl_reconciliation_status_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itl_reconciliation_status_history_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recon_id")
    private ITLReconciliationLog reconciliationLog;

    @Column(name = "recon_log_datetime")
    private Date reconLogDatetime;

    @Column(name = "recon_status_code")
    @Enumerated(EnumType.STRING)
    private ITLReconciliationStatus reconStatus;
}
