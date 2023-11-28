package gov.uk.ets.keycloak.users.service.application;

import java.util.List;

import org.keycloak.models.KeycloakSession;

import gov.uk.ets.keycloak.users.service.application.domain.Pageable;
import gov.uk.ets.keycloak.users.service.application.domain.UserFilter;
import gov.uk.ets.keycloak.users.service.application.domain.UserPersonalInfo;

/**
 * The application service interface
 */
public interface UkEtsUsersService {

    /**
     * Filters and retrieves uk-ets users
     * @param filter The users criteria
     * @return {@link Pageable<UserProjection>} results of filtering
     * @throws UkEtsUsersApplicationException
     */
    Pageable getUsers(UserFilter filter, KeycloakSession session) throws UkEtsUsersApplicationException;

    /**
     * Gets the personal info of the users that correspomd to the passed list of urid.
     * @param urids The list of urid
     * @return The list of {@link UserPersonalInfo}
     * @throws UkEtsUsersApplicationException
     */
    List<UserPersonalInfo> getUserPersonalInfos(List<String> urids, KeycloakSession session) throws UkEtsUsersApplicationException;

    /**
     * Filters and retrieves unregistered uk-ets users
     * @param filter The users created DateTime before
     * @return {@link List<UserEntity>} results of filtering
     * @throws UkEtsUsersApplicationException
     */
    List<String> fetchNonRegisteredUsersCreatedBefore(Long beforeDateTime, KeycloakSession session) throws UkEtsUsersApplicationException;
}
