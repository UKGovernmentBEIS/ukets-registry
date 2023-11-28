package gov.uk.ets.registry.api.common.model.services;

/**
 * Persistence service.
 */
public interface PersistenceService {

    /**
     * Saves the provided entity.
     * @param entity The entity.
     * @param <E> The parameterised type.
     * @return the persisted entity.
     */
    <E> E save(E entity);

    /**
     * Retrieves the next business identifier for the provided type.
     * @param aClass The class of the entity.
     * @return a number
     */
    Long getNextBusinessIdentifier(Class aClass);
}
