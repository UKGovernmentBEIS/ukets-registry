package gov.uk.ets.registry.api.itl.reconciliation.type;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Identifies the status of a Reconciliation Process.
 */
public enum ITLReconciliationStatus {

    /**
     * 0 - Confirmed.
     */
    CONFIRMED(0),

    /**
     * 1 - Initiated.
     */
    INITIATED(1),

    /**
     * 2 - Validated.
     */
    VALIDATED(2),

    /**
     * 3 - ITL Totals Inconsistent.
     */
    ITL_TOTAL_INCON(3),

    /**
     * 4 - ITL Unit Blocks Inconsistent.
     */
    ITL_UNIT_INCON(4),

    /**
     * 5 - ITL Completed.
     */
    ITL_COMPLETED(5),

    /**
     * 6 - ITL Completed with Manual Intervention.
     */
    ITL_COMPLETED_MAN_INT(6),

    /**
     * 7 - ITL Start Request Denied.
     */
    ITL_START_REQ_DENY(7),

    /**
     * 98 - Totals by account type inconsistent (See ITLCOLLAB-1047)
     */
    ITL_TOTALS_BY_ACC_TYPE_INC(98),

    /**
     * 99 - ITL manual intervention required (See ITLCOLLAB-776 for explanation)
     */
    ITL_MANUAL_INTERVENTION_REQUIRED(99),

    /**
     * Product of KP migration: 100 - Audit trails.
     */
    AUDIT_TRAILS(100, true),

    /**
     * Product of KP migration: 101 - Closed.
     */
    CLOSED(101, true),

    /**
     * Product of KP migration: 102 - Consolidated totals.
     */
    CONSOLIDATED_TOTALS(102, true),

    /**
     * Product of KP migration: 103 - Per account totals.
     */
    PER_ACCOUNT_TOTALS(103, true),

    /**
     * Product of KP migration: 104 - Snapshot taken.
     */
    SNAPSHOT_TAKEN(104, true),

    /**
     * Product of KP migration: 105 - Unit blocks.
     */
    UNIT_BLOCKS(105, true);

    /**
     * The code.
     */
    private Integer code;

    private boolean migrated = false;

    ITLReconciliationStatus(Integer code) {
        this.code = code;
    }

    ITLReconciliationStatus(Integer code, boolean migrated) {
        this.code = code;
        this.migrated = migrated;
    }

    private static final Map<Integer,ITLReconciliationStatus> codeToEnum = 
    	    Stream.of(values()).collect(Collectors.toMap(v -> v.getCode(),e -> e));
    
    /**
     * Returns the code.
     *
     * @return the code.
     */
    public Integer getCode() {
        return code;
    }
    
    public static Optional<ITLReconciliationStatus> fromCode(Integer code) {
    	return Optional.ofNullable(codeToEnum.get(code));
    }
}
