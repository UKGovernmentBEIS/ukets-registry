package gov.uk.ets.registry.api.transaction.service;

/**
 * Enumerates the various selection criteria.
 */
public enum SelectionCriteria {

    UNIT_TYPE("ub.type"),
    APPLICABLE_PERIOD("ub.applicablePeriod"),
    ORIGINAL_PERIOD("ub.originalPeriod"),
    PROJECT_IDENTIFIER("ub.projectNumber"),
    ORIGINATING_COUNTRY_CODE("ub.originatingCountryCode"),
    ACCOUNT_IDENTIFIER("ub.accountIdentifier"),
    SUBJECT_TO_SOP("ub.subjectToSop"),
    ENVIRONMENTAL_ACTIVITY("ub.environmentalActivity"),
    RESERVED_FOR_TRANSACTION("ub.reservedForTransaction"),
    EXCLUDE_APPLICABLE_PERIOD;

    /**
     * Constructor.
     */
    SelectionCriteria() {
        // nothing to implement here
    }

    /**
     * Constructor.
     * @param query The query.
     */
    SelectionCriteria(String query) {
        this.query = query;
    }

    /**
     * The query.
     */
    private String query;

    /**
     * Returns the query.
     * @return the query.
     */
    public String getQuery() {
        return query;
    }
}
