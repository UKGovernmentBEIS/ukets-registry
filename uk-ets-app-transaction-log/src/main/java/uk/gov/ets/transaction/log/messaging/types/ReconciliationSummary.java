package uk.gov.ets.transaction.log.messaging.types;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.ets.transaction.log.domain.type.ReconciliationStatus;

/**
 * Represents a reconciliation transfer object.
 */
@Setter
@Getter
@EqualsAndHashCode(of = "identifier")
public final class ReconciliationSummary {

    /**
     * The identifier.
     */
    private Long identifier;

    /**
     * The status.
     */
    private ReconciliationStatus status;

    /**
     * The reconciliation entries.
     */
    private List<ReconciliationEntrySummary> entries;

    /**
     * The reconciliation entries that failed.
     */
    private List<ReconciliationFailedEntrySummary> failedEntries;

}
