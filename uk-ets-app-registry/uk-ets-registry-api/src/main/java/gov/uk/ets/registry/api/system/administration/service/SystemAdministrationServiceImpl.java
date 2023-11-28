package gov.uk.ets.registry.api.system.administration.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.common.JsonMappingException;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.messaging.UktlAccountNotifyMessageService;
import gov.uk.ets.registry.api.messaging.domain.AccountNotification;
import gov.uk.ets.registry.api.system.administration.web.model.AccountsActionResultDTO;
import gov.uk.ets.registry.api.system.administration.web.model.ResetDatabaseActionResult;
import gov.uk.ets.registry.api.system.administration.web.model.UsersActionResultDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserAttributes;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.migration.UserRolesMigrator;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.PartialImportRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class SystemAdministrationServiceImpl implements SystemAdministrationService {

    @PersistenceContext
    private EntityManager entityManager;
    @Value("classpath:keycloak/uk-ets-realm-sample-users.json")
    private Resource ukEtsSampleUsers;
    @Value("classpath:sample-data/uk-ets-sample-accounts.json")
    private Resource ukEtsSampleAccounts;
    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthServerUrl;
    @Value("${keycloak.realm}")
    private String keycloakRealm;
    @Value("${keycloak.resource}")
    private String keycloakClientId;
    @Value("${REGISTRATION_API_PASSWORD:registration-api-user}")
    private String registrationApiPassword;
    /**
     * System admin urid as defined in json.
     */
    public static final String SYSTEM_ADMIN_URID = "UK873533391953";
    public static final String REGISTRATION_API_USER_URID = "UK694094547712";
    // these use are NOT going to be deleted from keycloak/database:
    private static final List<String> usersToKeep = List.of(SYSTEM_ADMIN_URID, REGISTRATION_API_USER_URID);
    public static final String REGISTRY_ADMINISTRATOR = "Registry Administrator";
    //See JIRA UKETS-3504 for these names
    public static final String UK_ETS_CENTRAL_ACCOUNT_HOLDER_NAME = "UK ETS Authority";
    public static final String UK_KP_ACCOUNT_HOLDER_NAME = "BEIS International";

    private final AuthorizationService authorizationServiceImpl;
    private final Mapper mapper;
    private final UserRolesMigrator userRolesMigrator;
    private final AccountService accountServiceImpl;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountAccessRepository accountAccessRepository;
    private final UktlAccountNotifyMessageService uktlAccountNotifyMessageService;
    private final AccountAccessService accountAccessService;


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ResetDatabaseActionResult reset() {
        log.debug("Resetting db state...");
        deleteEverything();
        resetKyotoProtocolUnitLimits();
        resetAllocationLimits();
        Keycloak keycloak = Keycloak.getInstance(keycloakAuthServerUrl, keycloakRealm, keycloakClientId,
            authorizationServiceImpl.getTokenString());
        UsersActionResultDTO usersResult = resetUsers(keycloak);
        syncSystemAdmin(keycloak);
        AccountsActionResultDTO accountsResult = createAccounts();
        log.debug("Reseting db state success...");
        return ResetDatabaseActionResult.
            builder().
            accountsResult(accountsResult).
            usersResult(usersResult).
            build();
    }

    private AccountsActionResultDTO createAccounts() {
        int sampleAccountsCreated = createSampleAccounts();

        return AccountsActionResultDTO.
            builder().
            governmentAccountsCreated(0).
            sampleAccountsCreated(sampleAccountsCreated).
            build();
    }

    private int createSampleAccounts() {
        try {
            List<AccountDTO> accounts = Arrays
                .asList(mapper.convertResToPojo(ukEtsSampleAccounts, AccountDTO[].class));

            accounts.forEach(t -> {
                var accountDTO = accountServiceImpl.openAccount(t);
                var byIdentifier = accountRepository.findByIdentifier(accountDTO.getIdentifier());
                byIdentifier.ifPresent(this::sendAccountNotification);
                byIdentifier.ifPresent(this::createRoleBasedAccountAccesses);
            });// end for each account
            return accounts.size();
        } catch (JsonMappingException e) {
            log.error("Error creating sample accounts.", e);
            throw SystemAdminActionException.create(SystemAdministrationActionError.builder()
                .code(SystemAdministrationActionError.ERROR_PARSING_JSON)
                .message("Could not parse accounts JSON file.")
                .build());
        }
    }

    private void createRoleBasedAccountAccesses(Account account) {
        accountAccessService.createAccountAccess(account, UserRole.getRolesForRoleBasedAccess(),
                                                 AccountAccessRight.ROLE_BASED);
    }

    private UsersActionResultDTO resetUsers(Keycloak keycloak) {

        int usersDeleted = clearExistingUsers(keycloak);
        int usersCreated = insertSampleUsers(keycloak);
        userRolesMigrator.migrate(true);

        return UsersActionResultDTO.builder().usersCreated(usersCreated).usersDeleted(usersDeleted).build();
    }

    /**
     * Inserts the users from the json file to both Keycloak and the local database.
     *
     * @param keycloak the Keycloak client
     * @return the number of users created.
     */
    private int insertSampleUsers(Keycloak keycloak) {
        try {
            PartialImportRepresentation realmUsersRepresentation =
                mapper.convertResToPojo(ukEtsSampleUsers, PartialImportRepresentation.class);
            realmUsersRepresentation.setIfResourceExists(PartialImportRepresentation.Policy.SKIP.toString());
            Response response = keycloak.realm(keycloakRealm).partialImport(realmUsersRepresentation);
            if (HttpStatus.SC_OK != response.getStatus()) {
                throw SystemAdminActionException.create(SystemAdministrationActionError.builder()
                    .code(SystemAdministrationActionError.ACCESS_NOT_ALLOWED)
                    .message("Could not execute partial import.Check Service Account Roles.")
                    .build());
            }

            // This list call does not fetch client level roles.
            List<UserRepresentation> users = keycloak.realm(keycloakRealm).users().list();
            users.stream()
                .filter(u -> usersToKeep.stream()
                    .noneMatch(urid -> urid.equalsIgnoreCase(u.getAttributes().get("urid").get(0))))
                .forEach(u -> {
                    User user = new User();
                    user.setIamIdentifier(u.getId());
                    user.setUrid(u.getAttributes().get(UserAttributes.URID.getAttributeName()).get(0));
                    user.setFirstName(u.getFirstName());
                    user.setLastName(u.getLastName());
                    user.setState(UserStatus
                        .valueOf(u.getAttributes().get(UserAttributes.STATE.getAttributeName()).get(0)));
                    if (u.getAttributes().get(UserAttributes.ALSO_KNOWN_AS.getAttributeName()) != null
                            && !u.getAttributes().get(UserAttributes.ALSO_KNOWN_AS.getAttributeName()).isEmpty()) {
                        user.setKnownAs(u.getAttributes().get(UserAttributes.ALSO_KNOWN_AS.getAttributeName()).get(0));
                    }

                    UserRepresentation userRepresentation =
                        realmUsersRepresentation.getUsers()
                                                .stream()
                                                .filter(t -> t.getUsername().equals(u.getUsername()))
                                                .findFirst()
                                                .orElse(new UserRepresentation());
                    // Avoid a second call to keycloak , instead fetch client level roles from json
                    if (isRegistryAdministrator(userRepresentation)) {
                        user.setDisclosedName(REGISTRY_ADMINISTRATOR);
                    } else {
                        if (!StringUtils.isEmpty(user.getKnownAs())) {
                            user.setDisclosedName(user.getKnownAs());
                        } else {
                            user.setDisclosedName(Utils.concat(" ", u.getFirstName(), u.getLastName()));
                        }
                    }
                    entityManager.persist(user);
                    if (isAuthority(userRepresentation)) {
                        createAuthorityAccountAccesses(user);
                    }
                });

            return users.size() - 1;
        } catch (JsonMappingException e) {
            log.error("Error in users JSON file.", e);
            throw SystemAdminActionException.create(SystemAdministrationActionError.builder()
                .code(SystemAdministrationActionError.ERROR_PARSING_JSON)
                .message("Could not parse users JSON file.")
                .build());
        }

    }

    private void createAuthorityAccountAccesses(User user) {
        findCentralAccounts()
            .forEach(a -> createAccountAccess(a, user, AccountAccessRight.INITIATE_AND_APPROVE));
        findReadOnlyCentralAccounts()
            .forEach(a -> createAccountAccess(a, user, AccountAccessRight.READ_ONLY));
    }

    private Iterable<Account> findReadOnlyCentralAccounts() {
        return accountRepository.findAll(QAccount.account.accountType
                                             .in(AccountType.getReadOnlyCentralTypes()
                                                            .stream()
                                                            .map(AccountType::getLabel)
                                                            .collect(Collectors.toList())));
    }

    private Iterable<Account> findCentralAccounts() {
        return accountRepository.findAll(QAccount.account.accountType
                                             .in(AccountType.getCentralTypes()
                                                            .stream()
                                                            .map(AccountType::getLabel)
                                                            .collect(Collectors.toList())));
    }

    private void createAccountAccess(Account account, User user, AccountAccessRight accessRight) {
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setUser(user);
        accountAccess.setState(AccountAccessState.ACTIVE);
        accountAccess.setRight(accessRight);
        accountAccess.setAccount(account);
        accountAccessRepository.save(accountAccess);
    }

    private boolean isRegistryAdministrator(UserRepresentation user) {
        if (user.getClientRoles() == null) {
            return false;
        }
        List<String> roles = Optional.ofNullable(user.getClientRoles().get(keycloakClientId)).orElse(new ArrayList<>());
        return roles.stream().anyMatch(roleBelongsToAdmin());
    }

    private boolean isAuthority(UserRepresentation user) {
        if (user.getClientRoles() == null) {
            return false;
        }
        List<String> roles = Optional.ofNullable(user.getClientRoles().get(keycloakClientId)).orElse(new ArrayList<>());
        return roles.stream().anyMatch(roleBelongsToAuthority());
    }

    private Predicate<String> roleBelongsToAdmin() {
        Predicate<String> rolePredicate = r -> UserRole.SENIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral().equals(r);
        return rolePredicate.or(r -> UserRole.JUNIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral().equals(r))
                            .or(r -> UserRole.READONLY_ADMINISTRATOR.getKeycloakLiteral().equals(r));
    }

    private Predicate<String> roleBelongsToAuthority() {
        return r -> UserRole.AUTHORITY_USER.getKeycloakLiteral().equals(r);
    }

    /**
     * Synchronises the system-admin user.
     *
     * @param keycloak the Keycloak client
     * @return true if the system admin was created false if it already existed.
     */
    private boolean syncSystemAdmin(Keycloak keycloak) {
        List<UserRepresentation> users = keycloak.realm(keycloakRealm).users().list();
        UserRepresentation keycloakSysAdmin = users.stream().
            filter(u -> SYSTEM_ADMIN_URID.equalsIgnoreCase(u.getAttributes().get("urid").get(0))).
            findFirst().
            orElseThrow();
        User systemAdmin = userRepository.findByUrid(SYSTEM_ADMIN_URID);
        if (Optional.ofNullable(systemAdmin).isEmpty()) {
            User user = new User();
            user.setIamIdentifier(keycloakSysAdmin.getId());
            user.setUrid(keycloakSysAdmin.getAttributes().get(UserAttributes.URID.getAttributeName()).get(0));
            user.setFirstName(keycloakSysAdmin.getFirstName());
            user.setLastName(keycloakSysAdmin.getLastName());
            user.setState(UserStatus.valueOf(
                keycloakSysAdmin.getAttributes().get(UserAttributes.STATE.getAttributeName()).get(0)));
            user.setDisclosedName(REGISTRY_ADMINISTRATOR);
            user.setKnownAs(
                    keycloakSysAdmin.getAttributes().get(UserAttributes.ALSO_KNOWN_AS.getAttributeName()).get(0));
            entityManager.persist(user);
            return true;
        } else {
            systemAdmin.setIamIdentifier(keycloakSysAdmin.getId());
            systemAdmin.setDisclosedName(REGISTRY_ADMINISTRATOR);
            return false;
        }
    }

    /**
     * Deletes the users from both Keycloak and the local database.
     *
     * @param keycloak the Keycloak client
     * @return the number of users deleted.
     */
    private int clearExistingUsers(Keycloak keycloak) {
        List<UserRepresentation> keycloakUsers = keycloak.realm(keycloakRealm).users().list();
        keycloakUsers.stream()
            .filter(u -> usersToKeep.stream()
                .noneMatch(urid -> urid.equalsIgnoreCase(u.getAttributes().get("urid").get(0))))
            .forEach(u -> keycloak.realm(keycloakRealm).users().get(u.getId()).remove());
        return entityManager.createQuery("DELETE FROM User u where u.urid NOT IN (:urids)")
            .setParameter("urids", usersToKeep)
            .executeUpdate();
    }

    private void deleteEverything() {
        entityManager.createQuery("DELETE FROM UserRoleMapping urm").executeUpdate();
        entityManager.createQuery("DELETE FROM AccountOwnership aown").executeUpdate();
        entityManager.createQuery("DELETE FROM InstallationOwnership iown").executeUpdate();
        entityManager.createQuery("DELETE FROM AccountHolderFile ahf").executeUpdate();
        entityManager.createQuery("DELETE FROM UserFile uf").executeUpdate();
        entityManager.createQuery("DELETE FROM TransactionConnection tc").executeUpdate();
        entityManager.createQuery("DELETE FROM TransactionMessage m").executeUpdate();
        entityManager.createQuery("DELETE FROM TransactionResponse r").executeUpdate();
        entityManager.createQuery("DELETE FROM TransactionHistory h").executeUpdate();
        entityManager.createQuery("DELETE FROM TransactionBlock tb").executeUpdate();
        entityManager.createQuery("DELETE FROM Transaction t").executeUpdate();

        entityManager.createQuery("DELETE FROM UnitBlock u").executeUpdate();

        entityManager.createQuery("DELETE FROM ITLNotificationHistory h").executeUpdate();
        entityManager.createQuery("DELETE FROM ITLNotificationBlock b").executeUpdate();
        entityManager.createQuery("DELETE FROM ITLNotification n").executeUpdate();

        entityManager.createQuery("DELETE FROM ITLSnapshotBlock b").executeUpdate();
        entityManager.createQuery("DELETE FROM ITLSnapshotLog g").executeUpdate();
        entityManager.createQuery("DELETE FROM ITLReconAuditTrailTxBlock b").executeUpdate();
        entityManager.createQuery("DELETE FROM ITLReconAuditTrailTxLog g").executeUpdate();
        entityManager.createQuery("DELETE FROM ITLRecoSequenceResponseLog g").executeUpdate();
        entityManager.createQuery("DELETE FROM ITLReconciliationStatusHistory h").executeUpdate();
        entityManager.createQuery("DELETE FROM ITLReconciliationLog g").executeUpdate();

        entityManager.createQuery("DELETE FROM AcceptMessageLog g").executeUpdate();

        entityManager.createQuery("DELETE FROM TrustedAccount ta").executeUpdate();
        entityManager.createQuery("DELETE FROM AccountAccess a").executeUpdate();
        entityManager.createQuery("DELETE FROM DomainEventEntity d").executeUpdate();
        entityManager.createQuery("DELETE FROM TaskSearchMetadata s").executeUpdate();
        entityManager.createQuery("DELETE FROM Task t").executeUpdate();
        entityManager.createQuery("UPDATE Account SET balance = :amount , unitType= :unitType ").
            setParameter("amount", 0L).
            setParameter("unitType", null).
            executeUpdate();

        List<RegistryAccountType> excludedRegistryTypes = new ArrayList<>();
        excludedRegistryTypes.add(RegistryAccountType.UK_AUCTION_DELIVERY_ACCOUNT);
        excludedRegistryTypes.add(RegistryAccountType.NONE);
        for (AccountType accountType : AccountType.getAllRegistryGovernmentTypes()) {
            excludedRegistryTypes.add(accountType.getRegistryType());
        }

        List<KyotoAccountType> excludedKyotoTypes = Stream.of(AccountType.values()).
            filter(AccountType::isGovernmentAccount).
            map(AccountType::getKyotoType).
            distinct().
            collect(Collectors.toList());

        entityManager.createQuery("DELETE FROM Account a where a.registryAccountType not in ?1").
            setParameter(1, excludedRegistryTypes).
            executeUpdate();
        entityManager
            .createQuery("DELETE FROM Account a where a.kyotoAccountType not in ?1 and a.registryAccountType = 'NONE'").
            setParameter(1, excludedKyotoTypes).
            executeUpdate();
        entityManager.createQuery("DELETE FROM AccountHolderRepresentative r").executeUpdate();
        List<String> govAccountHolderNames =
            Stream.of("UK Auction Platform", UK_ETS_CENTRAL_ACCOUNT_HOLDER_NAME, UK_KP_ACCOUNT_HOLDER_NAME)
                .collect(Collectors.toList());
        entityManager.createQuery("DELETE FROM AccountHolder h WHERE h.name not in ?1 or h.name is null").
            setParameter(1, govAccountHolderNames).
            executeUpdate();
        entityManager.createQuery("DELETE FROM AllocationEntry a").executeUpdate();
        entityManager.createQuery("DELETE FROM AllocationStatus s").executeUpdate();
        entityManager.createQuery("DELETE FROM Installation i").executeUpdate();
        entityManager.createQuery("DELETE FROM AircraftOperator o").executeUpdate();
        entityManager.createQuery("DELETE FROM AccountHolderRepresentative r").executeUpdate();
        entityManager.createQuery(
            "DELETE FROM Contact c where c.line1 <> '1 Victoria Street' and c.line1 <> 'Kyoto Gov Account Holder Contact'")
            .executeUpdate();
        entityManager.createQuery("DELETE FROM ReconciliationHistory rh").executeUpdate();
        entityManager.createQuery("DELETE FROM ReconciliationFailedEntry rfe").executeUpdate();
        entityManager.createQuery("DELETE FROM Reconciliation r").executeUpdate();
    }

    private void resetKyotoProtocolUnitLimits() {
        entityManager
            .createQuery("UPDATE RegistryLevel SET initialQuantity=10000000, consumedQuantity=0, pendingQuantity=0")
            .executeUpdate();
    }

    private void resetAllocationLimits() {
        entityManager
            .createQuery("UPDATE AllocationPhase SET initialPhaseCap=1000000, pendingPhaseCap=0, consumedPhaseCap=0")
            .executeUpdate();
        entityManager
            .createQuery("UPDATE AllocationYear SET initialYearlyCap=100000, pendingYearlyCap=0, consumedYearlyCap=0")
            .executeUpdate();
    }

    private void sendAccountNotification(Account account) {
        if (!RegistryAccountType.NONE.equals(account.getRegistryAccountType())) {
            uktlAccountNotifyMessageService
                .sendAccountOpeningNotification(AccountNotification.convert(account));
        }
    }
}
