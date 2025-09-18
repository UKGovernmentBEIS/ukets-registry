package gov.uk.ets.registry.api.itl.reconciliation.domain;

import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationPhase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "itl_reconciliation_log")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITLReconciliationLog {

    @Id
    @Column(name = "recon_id")
    private String reconId;

    @Column(name = "recon_snapshot_datetime")
    private Date reconSnapshotDatetime;

    @Column(name = "recon_action_begin_datetime")
    private Date reconActionBeginDatetime;

    @Column(name = "recon_action_end_datetime")
    private Date reconActionEndDatetime;

    @Column(name = "recon_phase_code")
    @Enumerated(EnumType.STRING)
    private ITLReconciliationPhase reconPhaseCode;

    @OneToMany(mappedBy = "reconciliationLog", fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<ITLReconciliationStatusHistory> reconciliationStatusHistories = new ArrayList<>();

    public void addHistoryEntry(ITLReconciliationStatusHistory historyEntry) {
        reconciliationStatusHistories.add(historyEntry);
        historyEntry.setReconciliationLog(this);
    }

    public ITLReconciliationStatusHistory getLatestHistoryEntry() {
        return this.getReconciliationStatusHistories().stream()
            .max(Comparator.comparing(ITLReconciliationStatusHistory::getReconLogDatetime))
            .orElseThrow(() -> new IllegalStateException(
                String.format("Reconciliation log history entries not found for reconciliation id: %s",
                    this.getReconId())));
    }
}
