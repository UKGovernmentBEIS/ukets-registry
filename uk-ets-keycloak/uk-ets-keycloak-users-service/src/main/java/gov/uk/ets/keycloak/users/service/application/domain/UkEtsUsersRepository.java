package gov.uk.ets.keycloak.users.service.application.domain;

import java.util.List;

import org.keycloak.models.jpa.entities.UserEntity;

/**
 * The uk-ets users repository
 */
public interface UkEtsUsersRepository {
    /**
     * Retrieves users that satisfy the passed criteria
     * @param filter The users criteria
     * @return The {@link Pageable} query results
     */
    Pageable fetchUsers(UserFilter filter);
    
    /**
     * Retrieves users that satisfy the passed criteria
     * @param beforeDateTime
     * @return
     */
    List<UserEntity> fetchNonRegisteredUsersCreatedBefore(Long beforeDateTime);
}
