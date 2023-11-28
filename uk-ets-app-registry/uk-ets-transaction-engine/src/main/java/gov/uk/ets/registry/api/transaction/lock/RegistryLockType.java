package gov.uk.ets.registry.api.transaction.lock;

import lombok.Getter;

/**
 * The type of the application lock
 */
@Getter
public enum RegistryLockType {
    /**
     * Lock related to the reconciliation process.
     */
    RECONCILIATION("reconciliation");

    /**
     * The key of the application lock.
     */
    private String key;

    RegistryLockType(String key) {
        this.key = key;
    }
}
