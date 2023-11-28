package gov.uk.ets.registry.api.itl.reconciliation.type;

/**
 * Reconciliation Phase Code - Identifies the phase of reconciliation to be
 * initiated.
 */
public enum ITLReconciliationPhase {

    /**
     * 1 - Totals.
     */
    TOTALS(1),

    /**
     * 2 - Unit Block Detail.
     */
    UNIT_BLOCK_DETAIL(2),

    /**
     * 3 - Trail.
     */
    TRAIL(3);

    /**
     * The code.
     */
    private Integer code;

    ITLReconciliationPhase(Integer code) {
        this.code = code;
    }

    /**
     * Returns the code.
     *
     * @return the code.
     */
    public Integer getCode() {
        return code;
    }

}
