package gov.uk.ets.registry.api.reconciliation.transfer;

import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a reconciliation transfer object.
 */
@Setter
@Getter
@EqualsAndHashCode(of = "identifier")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReconciliationSummary implements Serializable {
    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 8031136497700130363L;

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
