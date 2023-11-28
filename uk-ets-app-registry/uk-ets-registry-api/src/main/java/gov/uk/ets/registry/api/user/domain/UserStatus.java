package gov.uk.ets.registry.api.user.domain;

/**
 * Enumerates the various user states.
 */
public enum UserStatus {

    /**
     * Registered.
     */
    REGISTERED,

    /**
     * Validated.
     */
    VALIDATED,

    /**
     * Enrolled.
     */
    ENROLLED,

    /**
     * Un-enrollment is pending.
     */
    UNENROLLEMENT_PENDING,

    /**
     * Un-enrolled.
     */
    UNENROLLED,
    
    /**
     * Suspended.
     */
    SUSPENDED,
    
    /**
     * Deactivation is pending.
     */
    DEACTIVATION_PENDING,
    
    /**
     * Deactivated.
     */

    DEACTIVATED

    }
