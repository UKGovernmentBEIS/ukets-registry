package gov.uk.ets.registry.api.user.admin.service;

import static org.mockito.Mockito.doReturn;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.QAccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.authz.AuthorizationServiceImpl;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.admin.AssignUsersCriteriaDTO;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakClientRepresentationRepository;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakUserRepository;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakUserRepresentationRepository;
import gov.uk.ets.registry.api.user.admin.web.UserSearchByNameResultDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class UserAdministrationServiceImplTest {

    @Mock
    private AuthorizationServiceImpl authorizationServiceImpl;
    @Mock
    private AccountAccessRepository accountAccessRepository;
    @Mock
    private KeycloakUserRepository keycloakUserRepository;
    @Mock
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    @Mock
    private KeycloakUserRepresentationRepository keycloakUserRepresentationRepository;
    @Mock
    private KeycloakClientRepresentationRepository keycloakClientRepresentationRepository;

    UserAdministrationServiceImpl userAdministrationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userAdministrationService = Mockito.spy(
            new UserAdministrationServiceImpl(authorizationServiceImpl, accountAccessRepository, keycloakUserRepository,
                serviceAccountAuthorizationService, keycloakUserRepresentationRepository, keycloakClientRepresentationRepository));
    }

    @DisplayName("Check searching assignees for different task types and user roles. Should succeed.")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} user with role {1} is searching {2}")
    public void test_findAccountTaskAssignEntitledUsers(RequestType taskType, UserRole userRole, String userInput) {
        List<Long> requestIds = new ArrayList<>();
        requestIds.add(1L);
        requestIds.add(2L);
        List<Long> accountIdentifiers = new ArrayList<>();
        accountIdentifiers.add(1L);
        accountIdentifiers.add(2L);
        AssignUsersCriteriaDTO dto = new AssignUsersCriteriaDTO();
        dto.setRequestIds(requestIds);
        dto.setTerm(userInput);
        dto.setAccountIdentifiers(accountIdentifiers);

        User user1 =
            setUser(1L, "Authorized", "Reprentative 1", "UK405681794859", "1d3f0038-f36c-4169-afbe-d709fa45de66");
        User user2 =
            setUser(2L, "Authorized", "Reprentative 2", "UK566848116026", "9a416c9c-48c1-4fbc-9efd-84f6175eeff9");
        User user3 = setUser(3L, "User", "Enrolled", "UK813935774586", "d9b730d7-a497-487c-93fd-be40cdc6ebf4");

        /* Create Account 1 with two ARs */
        Account account1 = setAccount(1L, 10000014L, "UK-100-10000014-2-82");
        account1.getAccountAccesses().get(0).setUser(user1);
        account1.getAccountAccesses().get(1).setUser(user2);

        /* Create Account 2 with two ARs */
        Account account2 = setAccount(2L, 10000015L, "UK-100-10000015-2-77");
        account2.getAccountAccesses().get(0).setUser(user1);
        account2.getAccountAccesses().get(1).setUser(user3);

//        AccessToken accessToken = new AccessToken();
//        accessToken.setSubject("9a416c9c-48c1-4fbc-9efd-84f6175eeff9");
//        Mockito.when(authorizationServiceImpl.getToken()).thenReturn(accessToken);
        String subject = "9a416c9c-48c1-4fbc-9efd-84f6175eeff9";

        Set<AccountAccess> accountAccesses = new HashSet<>();
        accountAccesses.addAll(account1.getAccountAccesses());
        accountAccesses.addAll(account2.getAccountAccesses());
        accountAccesses = accountAccesses.stream()
            .filter(ar -> !ar.getUser().getIamIdentifier().equals(subject) &&
                (ar.getUser().getFirstName().toLowerCase().contains(dto.getTerm().toLowerCase()) ||
                    ar.getUser().getLastName().toLowerCase().contains(dto.getTerm().toLowerCase())))
            .collect(Collectors.toSet());
        Mockito.when(authorizationServiceImpl.getClaim(OAuth2ClaimNames.SUBJECT))
            .thenReturn(subject);
        Mockito.when(authorizationServiceImpl.hasClientRole(UserRole.SENIOR_REGISTRY_ADMINISTRATOR))
            .thenReturn(userRole.equals(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        Mockito.when(authorizationServiceImpl.hasClientRole(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR))
            .thenReturn(userRole.equals(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        Mockito.when(authorizationServiceImpl.hasClientRole(UserRole.AUTHORISED_REPRESENTATIVE))
            .thenReturn(userRole.equals(UserRole.AUTHORISED_REPRESENTATIVE));
        QAccountAccess accountAccess = QAccountAccess.accountAccess;
        Mockito.when(accountAccessRepository.findAll(accountAccess.account.identifier.in(dto.getAccountIdentifiers())
            .and(accountAccess.state.eq(AccountAccessState.ACTIVE))
            .and(accountAccess.user.iamIdentifier.notEqualsIgnoreCase(subject))
            .and(accountAccess.user.firstName.lower().contains(dto.getTerm().toLowerCase())
                .or(accountAccess.user.lastName.lower().contains(dto.getTerm().toLowerCase()))
                .or(accountAccess.user.knownAs.lower().contains(dto.getTerm().toLowerCase())))))
            .thenReturn(accountAccesses);

        getKeycloakUsersDependingOnTheRole(userRole);
        if (userRole.equals(UserRole.SENIOR_REGISTRY_ADMINISTRATOR)) {
            getKeycloakUsersDependingOnTheRole(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR);
        }

        List<UserSearchByNameResultDTO> assignees = userAdministrationService.findAccountTaskAssignEntitledUsers(dto);
        Assertions.assertNotEquals(null, taskType);
        if (userRole.equals(UserRole.SENIOR_REGISTRY_ADMINISTRATOR)) {
            if (userInput.equals("Administrator")) {
                Assertions.assertEquals(2, assignees.size());
                Assertions.assertEquals("Administrator Senior", assignees.get(0).getFullName());
            } else if (userInput.equals("Reprentative")) {
                Assertions.assertEquals(0, assignees.size());
            }
        } else if (userRole.equals(UserRole.AUTHORISED_REPRESENTATIVE)) {
            if (userInput.equals("Administrator")) {
                Assertions.assertEquals(0, assignees.size());
            } else if (userInput.equals("Reprentative")) {
                Assertions.assertEquals(0, assignees.size());
            }
        } else if (userRole.equals(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR)) {
            if (userInput.equals("Administrator")) {
                Assertions.assertEquals(1, assignees.size());
                Assertions.assertEquals("Administrator Junior", assignees.get(0).getFullName());
            } else if (userInput.equals("Reprentative")) {
                Assertions.assertEquals(0, assignees.size());
            }
        } else {
            Assertions.assertEquals(0, assignees.size());
        }
    }

    static Stream<Arguments> getArguments() {
        List<String> seachFields = new ArrayList<>();
        seachFields.add("Reprentative");
        seachFields.add("Administrator");
        return Stream.of(RequestType.values()).map(requestType ->
            Stream.of(UserRole.values()).map(userRole ->
                seachFields.stream().map(search -> Arguments.of(requestType, userRole, "Reprentative")))
        ).flatMap(Function.identity()).flatMap(Function.identity());
    }

    private void getKeycloakUsersDependingOnTheRole(UserRole userRole) {
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        Mockito.when(authorizationServiceImpl.obtainAccessToken()).thenReturn(accessTokenResponse);
        Set<UserRepresentation> userRepresentations = new HashSet<>();
        if (userRole.equals(UserRole.SENIOR_REGISTRY_ADMINISTRATOR)) {
            UserRepresentation senior = new UserRepresentation();
            senior.setFirstName("Administrator");
            senior.setLastName("Senior");
            senior.setEmail("uk-ets-senior-admin@trasys.gr");
            senior.setId("0495fb83-6266-4e25-bf5c-4698daec8af3");
            userRepresentations.add(senior);
            doReturn(userRepresentations).when(userAdministrationService).obtainUsers(userRole);
        } else if (userRole.equals(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR)) {
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setFirstName("Administrator");
            userRepresentation.setLastName("Junior");
            userRepresentation.setEmail("uk-ets-junior-admin@trasys.gr");
            userRepresentation.setId("72f55d86-f2d0-4d61-afea-f492209fd70b");
            userRepresentations.add(userRepresentation);
            doReturn(userRepresentations).when(userAdministrationService).obtainUsers(userRole);
        }
    }

    private User setUser(long id, String firstName, String lastName, String urid, String iamIdentifier) {
        User arUser = new User();
        arUser.setId(id);
        arUser.setFirstName(firstName);
        arUser.setLastName(lastName);
        arUser.setState(UserStatus.ENROLLED);
        arUser.setIamIdentifier(iamIdentifier);
        arUser.setUrid(urid);
        return arUser;
    }

    private Account setAccount(long id, long identifier, String fullIdentifier) {
        Account account = new Account();
        account.setId(id);
        account.setIdentifier(identifier);
        account.setAccountName("Account " + id);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        account.setAccountStatus(AccountStatus.OPEN);
        account.setOpeningDate(new Date());
        account.setStatus(Status.ACTIVE);
        account.setFullIdentifier(fullIdentifier);

        AccountAccess accountAccessAR = new AccountAccess();
        accountAccessAR.setRight(AccountAccessRight.APPROVE);
        accountAccessAR.setState(AccountAccessState.ACTIVE);
        accountAccessAR.setAccount(account);

        AccountAccess accountAccessAR2 = new AccountAccess();
        accountAccessAR2.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        accountAccessAR2.setState(AccountAccessState.ACTIVE);
        accountAccessAR2.setAccount(account);

        List<AccountAccess> accountAccesses = new ArrayList<>();
        accountAccesses.add(accountAccessAR);
        accountAccesses.add(accountAccessAR2);

        account.setAccountAccesses(accountAccesses);

        return account;
    }


}
