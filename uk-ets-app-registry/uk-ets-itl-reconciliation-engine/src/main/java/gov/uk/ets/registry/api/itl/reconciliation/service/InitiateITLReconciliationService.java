package gov.uk.ets.registry.api.itl.reconciliation.service;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationStatusHistory;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLSnapshotLog;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationStatusHistoryRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLSnapshotBlockRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLSnapshotLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationStatus;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.core.DefaultLockManager;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockConfigurationExtractor;
import net.javacrumbs.shedlock.core.LockManager;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;
import uk.gov.ets.lib.commons.kyoto.types.InitiateReconciliationRequest;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reconciliation service.
 */
@Service
@AllArgsConstructor
public class InitiateITLReconciliationService {

    private LockProvider lockProvider;
    private TaskScheduler taskScheduler;
    private ITLReconciliationLogRepository reconciliationRepository;
    private ITLReconciliationStatusHistoryRepository reconciliationStatusHistoryRepository;
    private ITLSnapshotLogRepository snapshotLogRepository;
    private ITLSnapshotBlockRepository snapshotBlockRepository;

    /**
     * Process an InitiateReconciliationRequest from the ITL.
     *
     * @param request InitiateReconciliationRequest
     */
    @Transactional
    public void initiateReconciliation(InitiateReconciliationRequest request) {
        ITLReconciliationLog reconciliationLog = persistITLReconciliationLog(request.getReconciliationIdentifier(),
            request.getSnapshotDatetime().getTime());
        persistITLReconciliationStatusHistory(reconciliationLog, ITLReconciliationStatus.CONFIRMED);
        ITLSnapshotLog snapshotLog = persistITLSnapshotLog(reconciliationLog);
        createSnapshotScheduler(snapshotLog);
    }

    private ITLReconciliationLog persistITLReconciliationLog(String reconIdentifier, Date snapshotDatetime) {
        ITLReconciliationLog reconciliationLog = new ITLReconciliationLog();
        reconciliationLog.setReconId(reconIdentifier);
        reconciliationLog.setReconSnapshotDatetime(snapshotDatetime);
        reconciliationLog.setReconActionBeginDatetime(new Date());

        return reconciliationRepository.save(reconciliationLog);
    }

    private ITLReconciliationStatusHistory persistITLReconciliationStatusHistory(ITLReconciliationLog reconciliationLog,ITLReconciliationStatus status) {
        ITLReconciliationStatusHistory reconHistory = new ITLReconciliationStatusHistory();
        reconHistory.setReconciliationLog(reconciliationLog);
        reconHistory.setReconLogDatetime(new Date());
        reconHistory.setReconStatus(status);
        return reconciliationStatusHistoryRepository.save(reconHistory);
    }

    private ITLSnapshotLog persistITLSnapshotLog(ITLReconciliationLog reconciliationLog) {
        ITLSnapshotLog snapshotLog = new ITLSnapshotLog();
        snapshotLog.setReconciliationLog(reconciliationLog);
        snapshotLog.setSnapshotDatetime(reconciliationLog.getReconSnapshotDatetime());

        return snapshotLogRepository.save(snapshotLog);
    }

    /**
     * Dynamically creates a Lockable (via ShedLock) Scheduler that is responsible for taking snapshot registry data.
     * @param snapshotLog the ITLSnapshotLog entry
     */
    private void createSnapshotScheduler(ITLSnapshotLog snapshotLog) {
        Instant lockAtMostUntil = snapshotLog.getSnapshotDatetime().toInstant().plus(5, ChronoUnit.MINUTES);
        Instant lockAtLeastUntil = snapshotLog.getSnapshotDatetime().toInstant();
        LockConfigurationExtractor lockConfigurationExtractor =
            (Runnable task) -> Optional.of(new LockConfiguration("ITLReconciliationSnapshotSchedulerLock",lockAtMostUntil,lockAtLeastUntil));
        LockManager lockManager = new DefaultLockManager(lockProvider,lockConfigurationExtractor);
        LockableTaskScheduler lockableTaskScheduler = new LockableTaskScheduler(taskScheduler,lockManager);
        lockableTaskScheduler.schedule(() -> takeSnapshotData(snapshotLog.getId()), snapshotLog.getSnapshotDatetime());
    }

    /**
     * Executes the actual SQL query that takes the snapshot
     * of unit block data.
     * @param snapshotLogId the identifier of the Snapshot Log
     */
    protected void takeSnapshotData(Long snapshotLogId) {
        snapshotBlockRepository.insertSnapshotBlocks(snapshotLogId);

        // TODO: should we set an internal reconciliation status here instead?
    }
}
