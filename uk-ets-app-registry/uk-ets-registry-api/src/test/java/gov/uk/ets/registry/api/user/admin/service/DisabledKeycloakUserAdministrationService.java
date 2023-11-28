package gov.uk.ets.registry.api.user.admin.service;

import gov.uk.ets.registry.api.common.model.entities.Contact;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "false")
public class DisabledKeycloakUserAdministrationService implements UserAdministrationService {

    @Override
    public UserRepresentation findByIamId(String id) {
        // TODO Implement method
        return null;
    }

    @Override
    public List<UserSearchByNameResultDTO> findAccountTaskAssignEntitledUsers(AssignUsersCriteriaDTO term) {
        return null;
    }

    @Override
    public KeycloakUser updateUserState(String userId, UserStatus userState) {
        // TODO Implement method
        return null;
    }

    @Override
    public boolean hasUserRole(String userId, String role) {
        return false;
    }

    @Override
    public void addUserRole(String userId, String role) {
        // TODO Implement method

    }

    @Override
    public void removeUserRole(String userId, String role) {

    }

    @Override
    public Contact findWorkContactDetailsByIamId(String id) {
        // TODO Implement method
        return null;
    }

    @Override
    public Contact findWorkContactDetailsByIamId(String id, boolean noUserToken) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeycloakUserSearchPagedResults search(KeycloakUserSearchCriteria criteria) {
        return null;
    }

    @Override
    public void updateUserEmail(String userId, String email) {

    }

    @Override
    public Optional<UserRepresentation> findByEmail(String email) {
        // TODO Implement method
        return null;
    }

    @Override
    public KeycloakUser updateUserState(String iamIdentifier, UserStatus userStatus, String token) {
        // TODO Implement method
        return null;
    }

    public boolean userExists(String email) {
        return false;
    }

    @Override
    public void updateUserDetails(UserRepresentation user) {
        // TODO Implement method

    }

    @Override
    public List<RoleRepresentation> getUserClientRoles(String iamIdentifier) {
        return null;
    }

    @Override
    public List<RoleRepresentation> getClientRoles() {
        return null;
    }

}
