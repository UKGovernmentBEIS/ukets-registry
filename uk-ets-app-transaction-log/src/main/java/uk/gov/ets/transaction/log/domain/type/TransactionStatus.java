package uk.gov.ets.transaction.log.domain.type;

/**
 * Enumerates the various transaction statuses.
 */
public enum TransactionStatus {

    /**
     * Awaiting approval.
     */
    AWAITING_APPROVAL(0, "Awaiting approval"),

    /**
     * Proposed.
     */
    PROPOSED(1, "Proposed"),

    /**
     * Checked no discrepancy.
     */
    CHECKED_NO_DISCREPANCY(2, "Checked no discrepancy"),

    /**
     * Checked discrepancy.
     */
    CHECKED_DISCREPANCY(3, "Checked discrepancy"),

    /**
     * Completed.
     */
    COMPLETED(4, "Completed"),

    /**
     * Terminated.
     */
    TERMINATED(5, "Terminated"),

    /**
     * Rejected.
     */
    REJECTED(6, "Rejected"),

    /**
     * Cancelled.
     */
    CANCELLED(7, "Cancelled"),

    /**
     * Accepted.
     */
    ACCEPTED(8, "Accepted"),

    /**
     * STL checked no discrepancy.
     */
    STL_CHECKED_NO_DISCREPANCY(9, "STL checked no discrepancy"),

    /**
     * STL checked discrepancy.
     */
    STL_CHECKED_DISCREPANCY(10, "STL checked discrepancy"),

    /**
     * Revered.
     */
    REVERSED(20, "Reversed"),

    /**
     * Delayed.
     */
    DELAYED(30, "Delayed");

    /**
     * The code.
     */
    private int code;

    /**
     * The value that is displayed to UI
     */
    private String label;

    /**
     * Constructor.
     * @param code The code.
     */
    TransactionStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * Returns the code.
     * @return the code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Parses the status from the provided code.
     * @param code The code.
     * @return a status.
     */
    public static TransactionStatus parse(int code) {
        TransactionStatus result = null;
        for (TransactionStatus status : values()) {
            if (status.getCode() == code) {
                result = status;
            }
        }
        return result;
    }

}