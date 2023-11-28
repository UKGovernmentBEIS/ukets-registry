package gov.uk.ets.registry.api.reconciliation.transfer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a failed reconciliation entry transfer object.
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class ReconciliationFailedEntrySummary extends ReconciliationEntrySummary {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 968410169770078476L;

    /**
     * The total quantity as calculated in the Registry.
     */
    private Long totalInRegistry;

    /**
     * The total quantity as calculated in the Transaction Log.
     */
    private Long totalInTransactionLog;

}
