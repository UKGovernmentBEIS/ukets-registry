package gov.uk.ets.registry.api.reconciliation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.domain.ReconciliationFailedEntry;
import gov.uk.ets.registry.api.reconciliation.domain.ReconciliationHistory;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationEntrySummaryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationFailedEntryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationHistoryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationEntrySummary;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationFailedEntrySummary;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Reconciliation domain service responsible for interacting with the domain repositories.
 */
@Service
@AllArgsConstructor
@Transactional
public class ReconciliationService {

    private final ReconciliationRepository reconciliationRepository;
    private final ReconciliationHistoryRepository reconciliationHistoryRepository;
    private final ReconciliationEntrySummaryRepository reconciliationEntrySummaryRepository;
    private final ReconciliationFailedEntryRepository reconciliationFailedEntryRepository;

    /**
     * Creates a new {@link Reconciliation} entity with {@linkReconciliationStatus#INITIATED} status.
     *
     * @return the unique business identifier of the created {@link Reconciliation} entity.
     */
    public Reconciliation createReconciliation(Date startDate) {
        Long identifier = reconciliationRepository.getNextIdentifier();
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setIdentifier(identifier);
        reconciliation.setCreated(startDate);
        reconciliation.setUpdated(new Date());
        reconciliation.setStatus(ReconciliationStatus.INITIATED);
        return reconciliationRepository.save(reconciliation);
    }

    /**
     * Creates a new {@link ReconciliationHistory} entity to audit the incoming reconciliation.
     *
     * @param reconciliation The {@link Reconciliation} entity to audit
     */
    public void keepHistory(Reconciliation reconciliation) {
        reconciliationHistoryRepository.save(ReconciliationHistory.builder()
            .reconciliation(reconciliation)
            .date(reconciliation.getUpdated())
            .status(reconciliation.getStatus())
            .build());
    }

    /**
     * Finds and returns the date that the last completed {@link Reconciliation} started.
     *
     * @return
     */
    public Date getLastReconciliationCompletedDate() {
        Reconciliation reconciliation = reconciliationRepository.fetchLastCompletedReconciliation();
        if (reconciliation != null) {
            return reconciliation.getCreated();
        }
        return null;
    }

    /**
     * Computes and returns the {@link ReconciliationEntrySummary} data transfer objects for a set of account business
     * identifiers.
     *
     * @param accountIdentifiers
     * @return The computed {@link ReconciliationEntrySummary} data transfer objects.
     */
    public List<ReconciliationEntrySummary> getReconciliationEntrySummaries(Set<Long> accountIdentifiers) {
        return reconciliationEntrySummaryRepository.fetch(accountIdentifiers);
    }

    /**
     * Deletes a {@link Reconciliation} entity.
     * @param identifier
     */
    public void deleteReconciliation(Long identifier) {
        reconciliationRepository.removeByIdentifier(identifier);
    }

    /**
     * Deletes all the {@link ReconciliationHistory} entries associated with the reconciliation of identifier.
     * @param identifier The identifier of the reconciliation.
     */
    public void deleteReconciliationHistory(Long identifier) {
        reconciliationHistoryRepository.deleteAllByReconciliationIdentifier(identifier);
    }

    /**
     * Updates the reconciliation described by the incoming reconciliation summary parameter.
     * @param reconciliationSummary The {@link ReconciliationSummary} object which describes the reconciliation that should be updated.
     * @return The {@link Reconciliation} entity
     */
    public Reconciliation updateReconciliation(ReconciliationSummary reconciliationSummary) {
        try {
            Reconciliation reconciliation = reconciliationRepository.findByIdentifier(reconciliationSummary.getIdentifier());
            List<ReconciliationFailedEntrySummary> failedEntrySummaries = reconciliationSummary.getFailedEntries();
            if(!CollectionUtils.isEmpty(failedEntrySummaries)) {
                failedEntrySummaries.stream()
                    .map(f -> {
                        ReconciliationFailedEntry failedEntry = new ReconciliationFailedEntry();
                        failedEntry.setAccountIdentifier(f.getAccountIdentifier());
                        failedEntry.setQuantityRegistry(f.getTotalInRegistry());
                        failedEntry.setQuantityTransactionLog(f.getTotalInTransactionLog());
                        failedEntry.setReconciliation(reconciliation);
                        failedEntry.setUnitType(f.getUnitType());
                        return failedEntry;
                    }).forEach(failedEntry -> {
                    reconciliationFailedEntryRepository.save(failedEntry);
                });
            }
            reconciliation.setData(new ObjectMapper().writeValueAsString(reconciliationSummary));
            reconciliation.setStatus(reconciliationSummary.getStatus());
            reconciliation.setUpdated(new Date());
            return reconciliationRepository.save(reconciliation);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Gets the latest started reconciliation
     * @return The latest started reconciliation
     */
    public Reconciliation getLatestReconciliation() {
        return reconciliationRepository.fetchLatestReconciliation();
    }

    /**
     * Makes the initiated reconciliations inconsistent.
     */
    public void closePendingReconciliations() {
        reconciliationRepository.findByStatus(ReconciliationStatus.INITIATED).stream()
            .forEach(r -> r.setStatus(ReconciliationStatus.INCONSISTENT));
    }
}
