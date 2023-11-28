package gov.uk.ets.registry.api.reconciliation.service;

import gov.uk.ets.registry.api.reconciliation.messaging.UKTLOutboundAdapter;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationEntrySummary;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsible for executing the reconciliation process by interacting with the UKTL.
 */
@AllArgsConstructor
@Log4j2
@Service
public class ProcessReconciliationService {
    private final PendingUKTransactionsChecker pendingUKTransactionsChecker;
    private final UKTLOutboundAdapter uktlOutboundAdapter;
    private final ReconciliationActionService reconciliationTaskService;

    /**
     * This is the first step of the reconciliation process. When this method is called a new reconciliation process
     * starts.
     */
    @Transactional
    public void initiate(Date startDate) {
        pendingUKTransactionsChecker.check();
        reconciliationTaskService.closePendingReconciliations();
        Long identifier = reconciliationTaskService.createReconciliation(startDate);
        List<ReconciliationEntrySummary> reconciliationEntrySummaries = reconciliationTaskService.calculateTotals();
        uktlOutboundAdapter.sendMessage(ReconciliationSummary.builder()
            .identifier(identifier)
            .status(ReconciliationStatus.INITIATED)
            .entries(reconciliationEntrySummaries)
            .build());
    }

    /**
     * This is the final step of the process where registry completes the reconciliation process.
     *
     * @param reconciliationSummary
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeReconciliation(ReconciliationSummary reconciliationSummary) {
        if(reconciliationSummary == null) {
            throw new IllegalStateException("The UKTL answer should not be null");
        }
        reconciliationTaskService.updateReconciliation(reconciliationSummary);
        if (reconciliationSummary.getStatus() == ReconciliationStatus.INCONSISTENT) {
            reconciliationTaskService.processFailedEntries(reconciliationSummary);
        }
    }
}
