package gov.uk.ets.registry.api.transaction.domain.type;

/**
 * Enumerates the various levels, limits and entitlements of the registry.
 */
public enum RegistryLevelType {

    /**
     * The levels for Kyoto Issuance.
     */
    ISSUANCE_KYOTO_LEVEL,

    /**
     * The entitlement for transferring AAUs.
     */
    AAU_TRANSFER,

    AAU_TO_RETIRE;
}
