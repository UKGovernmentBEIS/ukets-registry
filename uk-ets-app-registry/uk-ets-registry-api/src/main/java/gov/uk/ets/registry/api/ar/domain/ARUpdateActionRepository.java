package gov.uk.ets.registry.api.ar.domain;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.task.domain.types.RequestType;

import java.util.List;

/**
 * Repository for {@link ARUpdateAction} entities.
 */
public interface ARUpdateActionRepository {
    /**
     * Fetches the stored {@link ARUpdateAction} entities that correspond to the passed account identifier.
     * @param accountIdentifier The account unique business identifier. {@link Account#getIdentifier()}
     * @return The list of stored {@link ARUpdateAction} entities.
     */
    List<ARUpdateAction> fetchByAccountId(long accountIdentifier);
    
    /**
     * Fetches the stored add {@link ARUpdateAction} entities that correspond to the passed account identifier.
     * @param accountIdentifier The account unique business identifier. {@link Account#getIdentifier()}
     * @return The list of stored {@link ARUpdateAction} entities.
     */
    List<ARUpdateAction> fetchPendingArAdditionsByAccountId(long accountIdentifier);
    
    /**
     * Fetches the stored {@link ARUpdateAction} entities that correspond to the passed type.
     * @param type {@link RequestType}
     * @return The list of stored {@link ARUpdateAction} entities if the type refers to AR update task, otherwise an empty list.
     */
    List<ARUpdateAction> fetchPendingArUpdateActionsByType(RequestType type);
}
