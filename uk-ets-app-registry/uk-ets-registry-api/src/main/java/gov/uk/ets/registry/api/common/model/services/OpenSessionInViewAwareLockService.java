package gov.uk.ets.registry.api.common.model.services;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for locking persistence entities and which is aware about the usage of Open Session In View transactional pattern.
 */
@Service
public class OpenSessionInViewAwareLockService {
    private final EntityManager entityManager;
    private final boolean openSessionInViewEnabled;

    public OpenSessionInViewAwareLockService(EntityManager entityManager, @Value("${spring.jpa.open-in-view:true}") boolean openSessionInViewEnabled) {
        this.entityManager = entityManager;
        this.openSessionInViewEnabled = openSessionInViewEnabled;
    }

    /**
     * <p>
     *  Locks the underlying database table row of the entity with respect to given lock mode type and with specified
     *  properties.
     * </p>
     * <p>Fixes UKETS-3842</p>
     * <p>
     * When the Open Session In View pattern is enabled (the default mode), then it refreshes the state of the entity from the
     * database before locking it. The above is necessary because all the transactions that participate in the same http
     * request share the same Hibernate session which closes only when the request completes. As a result, if a previous
     * transaction in the same request had query the same entity and loaded it to the persistence context before, an {@link EntityManager#lock}
     * action on this cached entity from a following transaction would not obtain to lock it. This is why the entity should be reloaded from the
     * database to the persistence context first and after then to lock it.
     * </p>
     *
     * @param entity       The entity to lock
     * @param lockModeType The lock mode type
     * @param properties   The lock properties
     * @see EntityManager#lock
     * @see EntityManager#refresh
     */
    public void lock(Object entity, LockModeType lockModeType, Map properties) {
        if(openSessionInViewEnabled) {
            entityManager.refresh(entity, lockModeType, properties);
        } else {
            entityManager.lock(entity, lockModeType, properties);
        }
    }
}
