package gov.uk.ets.registry.api.user.admin.service;

import gov.uk.ets.registry.api.account.web.model.ContactDTO;
import gov.uk.ets.registry.api.user.KeycloakUser;
import gov.uk.ets.registry.api.user.admin.AssignUsersCriteriaDTO;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserSearchCriteria;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserSearchPagedResults;
import gov.uk.ets.registry.api.user.admin.web.UserSearchByNameResultDTO;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.List;
import java.util.Optional;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

/**
 * Service for users administration.
 */
public interface UserAdministrationService {

    /**
     * Finds the user in the Keycloak db with the provided id.
     *
     * @param id the IAM ID
     * @return a UserRepresentation
     * @since v.0.1.0
     */
    UserRepresentation findByIamId(String id);

    /**
     * Find users entitled for assignment in Account Create Task Type.
     *
     * @since v0.3.0
     */
    List<UserSearchByNameResultDTO> findAccountTaskAssignEntitledUsers(AssignUsersCriteriaDTO term);

    /**
     * Delegates to existing {@link #updateUserState(String, UserStatus)} method by providing
     * the Keycloak token for the current user.
     */
    KeycloakUser updateUserState(String userId, UserStatus userState);

    boolean hasUserRole(String userId, String role);

    /**
     * Updates the user with a new client role in Keycloak.
     *
     * @param userId the user ID
     * @param role   the new client role to be assigned to the user
     */
    void addUserRole(String userId, String role);

    /**
     * Removes the client role from the user of User ID.
     *
     * @param userId the user ID
     * @param role   the role to be removed.
     */
    void removeUserRole(String userId, String role);

    /**
     * Finds the work contact details of a user by the provided IAM id.
     *
     * @param id the IAM ID
     * @return the user work contact details.
     */
    ContactDTO findWorkContactDetailsByIamId(String id);

    /**
     * Finds the work contact details of a user by the provided IAM id.
     *
     * @param id          the IAM ID
     * @param noUserToken if true indicates that no logged in user exists.
     * @return the user work contact details.
     */
    ContactDTO findWorkContactDetailsByIamId(String id, boolean noUserToken);

    /**
     * Searches for users in keycloak.
     *
     * @param criteria the user search criteria
     * @return
     */
    KeycloakUserSearchPagedResults search(KeycloakUserSearchCriteria criteria,boolean withServiceAccountAccess);

    /***
     * Updates the user email, work email address.
     *
     * @param userId    the user ID.
     * @param email the new email.
     */
    void updateUserEmail(String userId, String email);


    /**
     * Retrieves a Keycloak representation of a user with the given email.
     * 
     * @deprecated email is unique per user but it can change,
     * use findByIamId instead
     */
    @Deprecated(forRemoval = true)
    Optional<UserRepresentation> findByEmail(String email);


    /**
     * Updates the user state in Keycloak.
     *
     * @param iamIdentifier the user ID.
     * @param userStatus    the new user state.
     * @param token         The token
     * @since v.0.5.0
     */
    KeycloakUser updateUserState(String iamIdentifier, UserStatus userStatus, String token);

    /**
     * Checks if the user of email exists in keycloak
     *
     * @param email The user email
     * @return true if user exists.
     */
    boolean userExists(String email);

    /***
     * Updates the user personal and work details.
     *
     * @param @{link  UserRepresentation }  the user representation.
     */
    void updateUserDetails(UserRepresentation user);


    /**
     * Get client roles for specific user iam id.
     */
    List<RoleRepresentation> getUserClientRoles(String iamIdentifier);

    /**
     * Get all client roles for specific user iam id.
     */
    List<RoleRepresentation> getClientRoles();


}
