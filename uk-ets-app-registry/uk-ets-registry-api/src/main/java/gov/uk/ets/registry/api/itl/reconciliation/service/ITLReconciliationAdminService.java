package gov.uk.ets.registry.api.itl.reconciliation.service;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationStatusHistory;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.web.model.ITLReconciliationDTO;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ITLReconciliationAdminService {

    private final ITLReconciliationLogRepository reconciliationLogRepository;

    public ITLReconciliationDTO getLatestReconciliation() {

        Optional<ITLReconciliationLog> itlReconciliationLog =
            reconciliationLogRepository.findByLatestSnapshotDatetime();
        if (itlReconciliationLog.isEmpty()) {
            return null;
        }

        ITLReconciliationLog reconciliation = itlReconciliationLog.get();
        ITLReconciliationStatusHistory latestReconciliationHistoryEntry = reconciliation.getLatestHistoryEntry();

        return ITLReconciliationDTO.builder()
            .identifier(reconciliation.getReconId())
            .created(reconciliation.getReconActionBeginDatetime())
            .updated(latestReconciliationHistoryEntry.getReconLogDatetime())
            .status(latestReconciliationHistoryEntry.getReconStatus())
            .readOnly(true)
            .build();
    }
}
