package gov.uk.ets.registry.api.account.domain.types;

public enum ComplianceStatus {
    A, // previously: COMPLIANT
    B, // previously: NEEDS_TO_SURRENDER
    C, // previously: NEEDS_TO_ENTER_EMISSIONS
    EXCLUDED,
    NOT_APPLICABLE, // previously: NOT_CALCULATED
    ERROR
}
