package gov.uk.ets.registry.api.transaction.lock;

import java.util.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.hibernate.LockOptions;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.stereotype.Service;

/**
 * Provides pessimistic locks to its client, the JPA transaction. The lock can be a shared (pessimistic read) lock
 * or an exclusive (pessimistic write) lock.
 */
@Service
@RequiredArgsConstructor
public class RegistryLockProvider {
    private final EntityManager entityManager;

    /**
     * The JPA transaction that calls this method acquires a shared pessimistic read lock of type of {@link RegistryLockType}.
     * A shared database lock is acquired to prevent any other transaction from acquiring an exclusive lock.
     * @param lockType The {@link RegistryLockType} type of lock
     * @param failFast True when the transaction should not wait (be blocked) until the lock is released.
     * @return The {@link RegistryLock} entity
     *
     * @throws {@link jakarta.persistence.LockTimeoutException} When the failFast flag is true and an other transaction has acquired a pessimistic write lock.
     */
    public RegistryLock acquirePessimisticReadLock(RegistryLockType lockType, boolean failFast) {
        return failFast ?
            entityManager.find(RegistryLock.class, lockType.getKey(), LockModeType.PESSIMISTIC_READ, Collections.singletonMap(
                        AvailableSettings.JPA_LOCK_TIMEOUT, LockOptions.NO_WAIT)) :
            entityManager.find(RegistryLock.class, lockType.getKey(), LockModeType.PESSIMISTIC_READ);
    }

    /**
     * The JPA transaction that calls this mehod acquires an exclusive pessimistic write lock.
     * An exclusive lock is acquired to prevent any other transaction from acquiring a shared or exclusive lock of the same type.
     *
     * @param lockType The {@link RegistryLockType} type of lock
     * @param failFast True when the transaction should not wait (be blocked) until the lock is released.
     * @return @return The {@link RegistryLock} entity
     *
     * @throws {@link jakarta.persistence.LockTimeoutException} When the failFast flag is true and one or more other
     *                transactions have acquired a pessimistic read or write lock of the same type.
     */
    public RegistryLock acquirePessimisticWriteLock(RegistryLockType lockType, boolean failFast) {
        return failFast ?
            entityManager.find(RegistryLock.class, lockType.getKey(), LockModeType.PESSIMISTIC_WRITE, Collections.singletonMap(
                        AvailableSettings.JPA_LOCK_TIMEOUT, LockOptions.NO_WAIT)) :
            entityManager.find(RegistryLock.class, lockType.getKey(), LockModeType.PESSIMISTIC_WRITE);
    }
}
