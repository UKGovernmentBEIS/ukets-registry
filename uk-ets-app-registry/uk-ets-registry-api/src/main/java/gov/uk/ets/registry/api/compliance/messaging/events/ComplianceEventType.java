package gov.uk.ets.registry.api.compliance.messaging.events;

public enum ComplianceEventType {
    ACCOUNT_CREATION,
    COMPLIANT_ENTITY_INITIALIZATION,
    UPDATE_OF_VERIFIED_EMISSIONS,
    SURRENDER,
    REVERSAL_OF_SURRENDER,
    EXCLUSION,
    REVERSAL_OF_EXCLUSION,
    UPDATE_OF_FIRST_YEAR_OF_VERIFIED_EMISSIONS,
    UPDATE_OF_LAST_YEAR_OF_VERIFIED_EMISSIONS,
    NEW_YEAR,
    //NOTE: we need to have a type for the static compliance event too,
    // so it can be handled together with the other events in the outbox
    STATIC_COMPLIANCE_REQUEST,
    GET_CURRENT_DYNAMIC_STATUS,
    RECALCULATE_DYNAMIC_STATUS
}

