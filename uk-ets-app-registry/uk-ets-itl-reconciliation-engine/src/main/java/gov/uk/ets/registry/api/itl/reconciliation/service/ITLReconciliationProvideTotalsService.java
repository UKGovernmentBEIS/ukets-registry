package gov.uk.ets.registry.api.itl.reconciliation.service;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationStatusHistory;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLSnapshotLog;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLCalculateTotalsRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLSnapshotLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationPhase;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.ets.lib.commons.kyoto.types.ProvideTotalsRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveTotalsRequest;
import uk.gov.ets.lib.commons.kyoto.types.Total;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ITLReconciliationProvideTotalsService {

    public static final Integer PROVIDE_TOTALS_WITHOUT_ACCOUNT_IDENTIFIER = 0;
    private final ITLCalculateTotalsRepository calculateTotalsRepository;
    private final ITLReconciliationLogRepository reconciliationLogRepository;
    private final ITLSnapshotLogRepository snapshotLogRepository;

    /**
     * Calculates totals and updates Reconciliation Log.
     */
    @Transactional
    @Retryable(
        value = IllegalStateException.class,
        backoff = @Backoff(delay = 1000), maxAttempts = 3
    )
    public ReceiveTotalsRequest provideTotals(ProvideTotalsRequest request) {

        updateReconciliationLog(request.getReconciliationIdentifier());

        ReceiveTotalsRequest receiveTotalsRequest = new ReceiveTotalsRequest();
        receiveTotalsRequest.setReconciliationIdentifier(request.getReconciliationIdentifier());

        receiveTotalsRequest.setMajorVersion(Constants.ITL_MAJOR_VERSION);
        receiveTotalsRequest.setMinorVersion(Constants.ITL_MINOR_VERSION);
        receiveTotalsRequest.setFrom(Constants.KYOTO_REGISTRY_CODE);
        receiveTotalsRequest.setTo(Constants.ITL_TO);

        if (PROVIDE_TOTALS_WITHOUT_ACCOUNT_IDENTIFIER.equals(request.getByAccountFlag())) {
            List<TotalDto> totals = calculateTotalsRepository.calculateTotals(request.getReconciliationIdentifier());
            receiveTotalsRequest.setTotals(totals.toArray(new Total[0]));
        }
        
        return receiveTotalsRequest;
    }

    /**
     * Updates log phase to TOTALS and adds new history entry with status INITIATED.
     */
    void updateReconciliationLog(String reconciliationIdentifier) {
        ITLReconciliationLog reconciliationLog =
            reconciliationLogRepository.findById(reconciliationIdentifier).orElseThrow(
                () -> new IllegalArgumentException("No reconciliation log found for id: " + reconciliationIdentifier)
            );

        ITLSnapshotLog itlSnapshotLog =
            snapshotLogRepository.findByReconciliationLog_ReconId(reconciliationIdentifier).orElseThrow(
                () -> new IllegalStateException(
                    String.format("Snapshot for reconciliation id: %s not found.", reconciliationIdentifier)
                )
            );
        log.info("Snapshot with id: {} found for reconciliation id: {}, proceeding with update", itlSnapshotLog.getId(),
            reconciliationIdentifier);

        ITLReconciliationStatusHistory historyEntry = new ITLReconciliationStatusHistory();
        historyEntry.setReconLogDatetime(new Date());
        historyEntry.setReconStatus(ITLReconciliationStatus.INITIATED);
        reconciliationLog.addHistoryEntry(historyEntry);
        reconciliationLog.setReconPhaseCode(ITLReconciliationPhase.TOTALS);
    }
}
