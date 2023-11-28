package uk.gov.ets.transaction.log.messaging.types;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.ets.transaction.log.domain.Reconciliation;
import uk.gov.ets.transaction.log.domain.ReconciliationFailedEntry;

/**
 * Represents a failed reconciliation entry transfer object.
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationFailedEntrySummary extends ReconciliationEntrySummary {

    public ReconciliationFailedEntrySummary(ReconciliationEntrySummary entry, Long totalInRegistry,
                                            Long totalInTransactionLog) {
        super(entry.getAccountIdentifier(), entry.getUnitType(), entry.getTotal());
        this.totalInRegistry = totalInRegistry;
        this.totalInTransactionLog = totalInTransactionLog;
    }

    /**
     * The total quantity as calculated in the Registry.
     */
    private Long totalInRegistry;

    /**
     * The total quantity as calculated in the Transaction Log.
     */
    private Long totalInTransactionLog;

    public ReconciliationFailedEntry toEntity(Reconciliation reconciliation) {
        ReconciliationFailedEntry failedEntry = new ReconciliationFailedEntry();
        failedEntry.setAccountIdentifier(this.getAccountIdentifier());
        failedEntry.setReconciliation(reconciliation);
        failedEntry.setUnitType(this.getUnitType());
        failedEntry.setQuantityRegistry(this.getTotalInRegistry());
        failedEntry.setQuantityTransactionLog(this.getTotalInTransactionLog());
        return failedEntry;
    }
}
