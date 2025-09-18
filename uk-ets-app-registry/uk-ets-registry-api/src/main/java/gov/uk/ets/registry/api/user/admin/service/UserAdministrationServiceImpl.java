package gov.uk.ets.registry.api.user.admin.service;

import static gov.uk.ets.registry.api.account.domain.QAccountAccess.accountAccess;

import com.querydsl.core.types.dsl.BooleanExpression;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.web.model.ContactDTO;
import gov.uk.ets.registry.api.authz.AuthorizationServiceImpl;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.KeycloakUser;
import gov.uk.ets.registry.api.user.admin.AssignUsersCriteriaDTO;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakClientRepresentationRepository;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakUserRepository;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakUserRepresentationRepository;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserSearchCriteria;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserSearchPagedResults;
import gov.uk.ets.registry.api.user.admin.web.UserSearchByNameResultDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserAttributes;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


/**
 * Implementation for User Administration service.
 */
@Service
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
@RequiredArgsConstructor
public class UserAdministrationServiceImpl implements UserAdministrationService {
    private static final int FIRST_ELEMENT = 0;
    public static final String REMOVED = "REMOVED_DUE_TO_STATUS";
    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthServerUrl;
    @Value("${keycloak.realm}")
    private String keycloakRealm;
    @Value("${keycloak.resource}")
    private String keycloakClientId;

    private final AuthorizationServiceImpl authorizationServiceImpl;
    private final AccountAccessRepository accountAccessRepository;
    private final KeycloakUserRepository keycloakUserRepository;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final KeycloakUserRepresentationRepository keycloakUserRepresentationRepository;
    private final KeycloakClientRepresentationRepository keycloakClientRepresentationRepository;

    /**
     * Finds the user in the Keycloak db with the provided id.
     *
     * @param id the IAM ID
     * @return a UserRepresentation
     * @since v.0.1.0
     */
    @Override
    public UserRepresentation findByIamId(String id) {
        return keycloakUserRepresentationRepository.fetchUserDataById(accessToken(), id);
    }


    /**
     * This function returns a list of possible assignees depending on the general rules below.
     * <ul>
     *      <li> If the user is a senior registry administrator, the list includes the following:
     *              Senior registry administrators, Junior registry administrators
     *      </li>
     *      <li> If the user is a junior registry administrator, the list includes the following:
     *              Junior registry administrators
     *      </li>
     *      <li>If the tasks is related to an account and the user is the AR of that account, the list includes the
     *      following:
     *              ARs of the same account
     *      </li>
     * </ul>
     *
     * @param criteria The user selection from the UI.
     * @return A list of assignees {@link AssignUsersCriteriaDTO}
     */
    @Override
    @Transactional
    public List<UserSearchByNameResultDTO> findAccountTaskAssignEntitledUsers(AssignUsersCriteriaDTO criteria) {
        String iamId = authorizationServiceImpl.getClaim(OAuth2ClaimNames.SUBJECT);
        boolean isSeniorAdmin = authorizationServiceImpl.hasClientRole(UserRole.SENIOR_REGISTRY_ADMINISTRATOR);
        boolean isJuniorAdmin = authorizationServiceImpl.hasClientRole(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR);
        boolean isAR = authorizationServiceImpl.hasClientRole(UserRole.AUTHORISED_REPRESENTATIVE);
        boolean isAuthorityUser = authorizationServiceImpl.hasClientRole(UserRole.AUTHORITY_USER);

        List<UserSearchByNameResultDTO> assignees = new ArrayList<>();
        if (isAR) {
            assignees
                .addAll(getMatchedUserByAccount(criteria, iamId, accountAccess.state.eq(AccountAccessState.ACTIVE)
                        .and(accountAccess.right.ne(AccountAccessRight.ROLE_BASED))));
        } else if (isSeniorAdmin) {
            assignees.addAll(getMatchedUserByRole(iamId, criteria.getTerm(), UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
            assignees.addAll(getMatchedUserByRole(iamId, criteria.getTerm(), UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
            if (criteria.getTaskTypes() != null &&
                criteria.getTaskTypes().contains(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD)) {
                assignees
                    .addAll(getMatchedUserByAccount(criteria, iamId, accountAccess.state.eq(AccountAccessState.ACTIVE)
                        .or(accountAccess.state.eq(AccountAccessState.REQUESTED))));
            }
        } else if (isJuniorAdmin) {
            assignees.addAll(getMatchedUserByRole(iamId, criteria.getTerm(), UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
            if (criteria.getTaskTypes() != null &&
                criteria.getTaskTypes().contains(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD)) {
                assignees
                    .addAll(getMatchedUserByAccount(criteria, iamId, accountAccess.state.eq(AccountAccessState.ACTIVE)
                        .or(accountAccess.state.eq(AccountAccessState.REQUESTED))));
            }
        } else if (isAuthorityUser) {
            assignees.addAll(getMatchedUserByRole(iamId, criteria.getTerm(), UserRole.AUTHORITY_USER));
        }
        assignees.sort(Comparator.comparing(UserSearchByNameResultDTO::getFullName));
        return assignees;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Returns a Set of assignees  depending on the user role and the searching value.
     *
     * @param iamId    The current user that is searching the users to assign the task.
     * @param term     The searching value.
     * @param userRole Obtain the users with the specific role.
     * @return Set of assignees {@link UserSearchByNameResultDTO}
     */
    private Set<UserSearchByNameResultDTO> getMatchedUserByRole(String iamId, String term, UserRole userRole) {
        return obtainUsers(userRole)
            .stream()
            .filter(u -> !u.getId().equals(iamId))
            .filter(u -> u.getFirstName() != null && u.getLastName() != null)
            .filter(nameContainsTerm(term))
            .map(this::toAssignedUser).collect(Collectors.toSet());
    }

    private boolean knownAsExists(UserRepresentation u) {
        return u.getAttributes() != null 
                && u.getAttributes().get(UserAttributes.ALSO_KNOWN_AS.getAttributeName()) != null
                && !u.getAttributes().get(UserAttributes.ALSO_KNOWN_AS.getAttributeName()).isEmpty()
                && !StringUtils.isEmpty(u.getAttributes().get(UserAttributes.ALSO_KNOWN_AS.getAttributeName()).get(0));
    }


	/**
     * Returns a Set of assignees depending on the account and the searching value.
     *
     * @param criteria The user selection from the UI.
     * @param iamId    The current user that is searching the users to assign the task.
     *                 param  accountAccessStatePredicate   Account access state boolean expression
     * @return Set of assignees {@link UserSearchByNameResultDTO}
     */
    private Set<UserSearchByNameResultDTO> getMatchedUserByAccount(AssignUsersCriteriaDTO criteria, String iamId,
                                                                   BooleanExpression accountAccessStatePredicate) {
        return StreamSupport.stream(
            accountAccessRepository.findAll(accountAccess.account.identifier.in(criteria.getAccountIdentifiers())
                .and(accountAccessStatePredicate)
                .and(accountAccess.user.iamIdentifier.notEqualsIgnoreCase(iamId))
                .and(accountAccess.user.firstName.lower().contains(criteria.getTerm().toLowerCase())
                    .or(accountAccess.user.lastName.lower().contains(criteria.getTerm().toLowerCase()))
                    .or(accountAccess.user.knownAs.lower().contains(criteria.getTerm().toLowerCase())))
            ).spliterator(), false)
            .filter(distinctByKey(ar -> ar.getUser().getUrid()))
            .map(ar -> toAssignedUser(ar.getUser()))
            .collect(Collectors.toSet());
    }

    /**
     * Delegates to  {@link #updateUserState(String, UserStatus, String)} method by providing
     * the Keycloak token for the current user.
     */
    @Override
    public KeycloakUser updateUserState(String userId, UserStatus userState) {
        return updateUserState(userId, userState, accessToken());
    }

    @Override
    public boolean hasUserRole(String userId, String role) {
        ClientRepresentation client = getClientsByClientId().get(0);
        List<RoleRepresentation> roleRepresentations = keycloakUserRepresentationRepository
                .fetchRoleDataByUserIdAndClient(accessToken(), userId, client.getId());
        return roleRepresentations.stream()
            .anyMatch(r -> r.getName().equals(role));
    }

    /**
     * Updates the user with a new client role in Keycloak.
     *
     * @param userId the user ID
     * @param role   the new client role to be assigned to the user.
     */
    @Override
    public void addUserRole(String userId, String role) {
        ClientRepresentation client = getClientsByClientId().get(0);
        String clientID = client.getId();
        RoleRepresentation clientRole = keycloakClientRepresentationRepository.fetchRoleDataByClientIdAndRoleName(accessToken(), clientID, role);
        keycloakUserRepresentationRepository.addRoleDataByUserIdAndClient(accessToken(), userId, clientID, Collections.singletonList(clientRole));
    }

    /**
     * Removes the client role from the user of User ID.
     *
     * @param userId the user ID
     * @param role   the role to be removed.
     */
    @Override
    public void removeUserRole(String userId, String role) {
        ClientRepresentation client = getClientsByClientId().get(0);
        String clientID = client.getId();
        RoleRepresentation clientRole = keycloakClientRepresentationRepository
                .fetchRoleDataByClientIdAndRoleName(accessToken(), clientID, role);
        keycloakUserRepresentationRepository.deleteRoleDataByUserIdAndClient(accessToken(), userId, clientID, Collections.singletonList(clientRole));
    }


    @Override
    public ContactDTO findWorkContactDetailsByIamId(String id, boolean noUserToken) {
        ContactDTO userWorkContact = new ContactDTO();
        UserRepresentation user = noUserToken ? serviceAccountAuthorizationService.getUser(id) : findByIamId(id);
        if (!CollectionUtils.isEmpty(user.getAttributes())) {
            userWorkContact.setLine1(getValue(user, UserAttributes.WORK_BUILDING_AND_STREET));
            userWorkContact.setLine2(getValue(user, UserAttributes.WORK_BUILDING_AND_STREET_OPTIONAL));
            userWorkContact.setLine3(getValue(user, UserAttributes.WORK_BUILDING_AND_STREET_OPTIONAL2));
            userWorkContact.setPostCode(getValue(user, UserAttributes.WORK_POST_CODE));
            userWorkContact.setCity(getValue(user, UserAttributes.WORK_TOWN_OR_CITY));
            userWorkContact.setStateOrProvince(getValue(user, UserAttributes.WORK_STATE_OR_PROVINCE));
            userWorkContact.setCountry(getValue(user, UserAttributes.WORK_COUNTRY));
            userWorkContact.setMobileCountryCode(getValue(user, UserAttributes.WORK_MOBILE_COUNTRY_CODE));
            userWorkContact.setMobilePhoneNumber(getValue(user, UserAttributes.WORK_MOBILE_PHONE_NUMBER));
            userWorkContact.setAlternativeCountryCode(getValue(user, UserAttributes.WORK_ALTERNATIVE_COUNTRY_CODE));
            userWorkContact.setAlternativePhoneNumber(getValue(user, UserAttributes.WORK_ALTERNATIVE_PHONE_NUMBER));
            userWorkContact.setNoMobilePhoneNumberReason(getValue(user, UserAttributes.NO_MOBILE_PHONE_NUMBER_REASON));
            userWorkContact.setEmailAddress(user.getEmail());
        }
        return userWorkContact;
    }

    @Override
    public ContactDTO findWorkContactDetailsByIamId(String id) {
        return this.findWorkContactDetailsByIamId(id, false);
    }

    /**
     * Extracts an attribute value from the user representation.
     *
     * @param user      The user.
     * @param attribute The attribute.
     * @return the value.
     */
    private String getValue(UserRepresentation user, UserAttributes attribute) {
        String result = null;
        final List<String> list = user.getAttributes().get(attribute.getAttributeName());
        if (!CollectionUtils.isEmpty(list)) {
            result = list.get(0);
        }
        return result;
    }

    /**
     * Obtain from keycloak the users the have the specified client roleName.
     *
     * @param role the registry role
     * @return a set of UserRepresentation
     * @since v.0.3.0
     */
    public Set<UserRepresentation> obtainUsers(UserRole role) {
        String id = getClientsByClientId().get(FIRST_ELEMENT).getId();
        return keycloakClientRepresentationRepository.fetchUserDataByClientAndRole(accessToken(), id, role.getKeycloakLiteral());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeycloakUserSearchPagedResults search(KeycloakUserSearchCriteria criteria,boolean withServiceAccountAccess) {
        String accessToken = "";
        if(withServiceAccountAccess){
            accessToken = serviceAccountAuthorizationService.obtainAccessToken().getToken();
        } else{
            accessToken = accessToken();
        }
        return keycloakUserRepository.fetch(criteria, accessToken);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserEmail(String oldEmailAddress, String newEmailAddress) {
        //Fetch user via email not userId
        Optional<UserRepresentation> userRepresentationOptional = findByEmail(oldEmailAddress);
        if(userRepresentationOptional.isPresent()) {
            UserRepresentation userRepresentation = userRepresentationOptional.get();
            userRepresentation.setEmail(newEmailAddress);
            keycloakUserRepresentationRepository.updateUser(accessToken(), userRepresentation);            
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserDetails(UserRepresentation user) {
        keycloakUserRepresentationRepository.updateUser(accessToken(), user);
    }

    /**
     * Finds the user in the Keycloak db with the provided email.
     * Keycloak UsersResource.search() is by username but the username matches the email in our case.
     * Copied from registration module.
     */
    @Override
    public Optional<UserRepresentation> findByEmail(String email) {
        List<UserRepresentation> users = keycloakUserRepresentationRepository
                .fetchUserDataByUserName(serviceAccountAuthorizationService.obtainAccessToken().getToken(), email);
        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(users.get(FIRST_ELEMENT));
        }
    }

    /**
     * Updates the user state in Keycloak.
     * Note that it also enables or disables the user according to the provided status.
     *
     * @param userId    the user ID.
     * @param userState the new user state.
     * @param token     token used by keycloak to authenticate user
     * @since v.0.5.0
     */
    @Override
    public KeycloakUser updateUserState(String userId, UserStatus userState, String token) {
        UserRepresentation userRepresentation =  keycloakUserRepresentationRepository.fetchUserDataById(token, userId);
        deleteHomeAddress(userRepresentation, userState);
        userRepresentation.getAttributes().replace(UserAttributes.STATE.getAttributeName(), Collections.singletonList(userState.name()));
        userRepresentation.setEnabled(!Set.of(
                UserStatus.SUSPENDED, UserStatus.DEACTIVATION_PENDING, UserStatus.DEACTIVATED).contains(userState));
        keycloakUserRepresentationRepository.updateUser(token, userRepresentation);

        return new KeycloakUser(userRepresentation);
    }

    /**
     * According to UKETS-4789 the home address should be deleted when the user is Validated. Only in Registered state
     * the home address remains.
     *
     * @param userRepresentation The user object
     * @param updatedState       The updated state
     */
    private void deleteHomeAddress(UserRepresentation userRepresentation, UserStatus updatedState) {
        if (!CollectionUtils.isEmpty(userRepresentation.getAttributes().get(UserAttributes.STATE.getAttributeName()))) {
            String currentState = userRepresentation.getAttributes().get(UserAttributes.STATE.getAttributeName()).get(0);
            if (UserStatus.SUSPENDED.equals(updatedState) && !UserStatus.REGISTERED.name().equals(currentState) ||
                !UserStatus.REGISTERED.equals(updatedState) && UserStatus.SUSPENDED.name().equals(currentState) ||
                UserStatus.VALIDATED.equals(updatedState) && UserStatus.REGISTERED.name().equals(currentState) ||
                UserStatus.DEACTIVATED.equals(updatedState)) {
                userRepresentation.getAttributes().replace(UserAttributes.BUILDING_AND_STREET.getAttributeName(), Collections.singletonList(REMOVED));
                userRepresentation.getAttributes()
                    .replace(UserAttributes.BUILDING_AND_STREET_OPTIONAL.getAttributeName(), Collections.singletonList(REMOVED));
                userRepresentation.getAttributes()
                    .replace(UserAttributes.BUILDING_AND_STREET_OPTIONAL2.getAttributeName(), Collections.singletonList(REMOVED));
                userRepresentation.getAttributes().replace(UserAttributes.COUNTRY.getAttributeName(), Collections.singletonList(REMOVED));
                userRepresentation.getAttributes().replace(UserAttributes.TOWN_OR_CITY.getAttributeName(), Collections.singletonList(REMOVED));
                userRepresentation.getAttributes().replace(UserAttributes.STATE_OR_PROVINCE.getAttributeName(), Collections.singletonList(REMOVED));
                userRepresentation.getAttributes().replace(UserAttributes.POST_CODE.getAttributeName(), Collections.singletonList(REMOVED));
            }
        }
    }

    private UserSearchByNameResultDTO toAssignedUser(UserRepresentation u) {

        UserSearchByNameResultDTO assignUser = new UserSearchByNameResultDTO();

        if (u.getAttributes() != null && !u.getAttributes().isEmpty() && !u.getAttributes()
            .get(UserAttributes.URID.getAttributeName()).isEmpty()) {
            assignUser.setUrid(u.getAttributes().get(UserAttributes.URID.getAttributeName()).get(FIRST_ELEMENT));
        }
        String fullName = u.getFirstName() + ' ' + u.getLastName();
        String knownAs = knownAsExists(u) ? u.getAttributes().get(UserAttributes.ALSO_KNOWN_AS.getAttributeName()).get(0) : "";
        assignUser.setFullName(fullName);
        assignUser.setKnownAs(knownAs);
        assignUser.setDisplayName(knownAsExists(u)
                ? knownAs
                : fullName);

        return assignUser;
    }

    private UserSearchByNameResultDTO toAssignedUser(User u) {

        UserSearchByNameResultDTO assignUser = new UserSearchByNameResultDTO();
        assignUser.setUrid(u.getUrid());
        String fullName = u.getFirstName() + ' ' + u.getLastName();
        String knownAs = u.getKnownAs();
        boolean knownAsExists = u.getKnownAs() != null && !StringUtils.isEmpty(u.getKnownAs());
        assignUser.setFullName(fullName);
        assignUser.setKnownAs(knownAs);
        assignUser.setDisplayName(knownAsExists ? knownAs : fullName);

        return assignUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean userExists(String email) {
        return findByEmail(email).isPresent();
    }

    public List<RoleRepresentation> getUserClientRoles(String iamIdentifier) {
        Keycloak keycloak = Keycloak.getInstance(keycloakAuthServerUrl, keycloakRealm, keycloakClientId,
            serviceAccountAuthorizationService.obtainAccessToken().getToken());
        return keycloak.realm(keycloakRealm).users().get(iamIdentifier).roles()
            .clientLevel(getClientUUID())
            .listEffective();
    }

    public List<RoleRepresentation> getClientRoles() {
        Keycloak keycloak = Keycloak.getInstance(keycloakAuthServerUrl, keycloakRealm, keycloakClientId,
            serviceAccountAuthorizationService.obtainAccessToken().getToken());
        return keycloak.realm(keycloakRealm).clients().get(getClientUUID()).roles().list();
    }

    public String getClientUUID() {
        Keycloak keycloak = Keycloak.getInstance(keycloakAuthServerUrl, keycloakRealm, keycloakClientId,
            serviceAccountAuthorizationService.obtainAccessToken().getToken());
        return keycloak.realm(keycloakRealm).clients().findByClientId(keycloakClientId).get(FIRST_ELEMENT).getId();
    }

    private Predicate<UserRepresentation> nameContainsTerm(String term) {
        Predicate<UserRepresentation> predicate = u -> u.getFirstName().toLowerCase().contains(term.toLowerCase());
        return predicate.or(u -> u.getLastName().toLowerCase().contains(term.toLowerCase()))
            .or(u -> Utils.concat(" ", u.getFirstName(), u.getLastName()).toLowerCase().contains(term.toLowerCase()))
            .or(u -> u.getAttributes() != null && u.getAttributes().get(UserAttributes.ALSO_KNOWN_AS.getAttributeName()) != null 
            && u.getAttributes().get(UserAttributes.ALSO_KNOWN_AS.getAttributeName()).get(0).toLowerCase().contains(term.toLowerCase()));
    }

    private String accessToken() {
        return authorizationServiceImpl.obtainAccessToken().getToken();
    }

    private List<ClientRepresentation> getClientsByClientId() {
        return keycloakClientRepresentationRepository.fetchClientDataByClientId(accessToken(), keycloakClientId);
    }
}
