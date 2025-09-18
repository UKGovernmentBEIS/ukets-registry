package gov.uk.ets.registry.api.account.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.domain.AccountOwnership;
import gov.uk.ets.registry.api.account.domain.InstallationOwnership;
import gov.uk.ets.registry.api.account.domain.InstallationOwnershipStatus;
import gov.uk.ets.registry.api.account.domain.types.AccountOwnershipStatus;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.test.RegistryIntegrationTest;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;

@RegistryIntegrationTest
@TestPropertySource(locations = "/integration-test-application.properties", properties = {
    "keycloak.enabled=false"
})
@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountServiceIntegrationTest {

    private final String accountOpeningTestDataClassPath = "classpath:/test-data/account-opening/";

    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @MockBean
    UserService userService;

    @MockBean
    AuthorizationService authorizationService;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private AccountService accountService;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp(){
        User currentUser = new User();
        currentUser.setIamIdentifier("9a4ae5f6-2a68-42b2-94db-786db27daaf4");
        when(userService.getCurrentUser()).thenReturn(currentUser);
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("senior-registry-administrator");
        List<RoleRepresentation> list = new ArrayList<>();
        list.add(roleRepresentation);
        when(authorizationService.getClientLevelRoles(currentUser.getIamIdentifier())).thenReturn(list);
    }


    @DisplayName("Open installation account")
    @Order(1)
    @Test
    void testOpenAccountInstallationAccount() {

        //when
        AccountDTO newAccount = accountService.openAccount(this.readAccountFromJson("installation.json"));

        //then

        assertEquals(AccountStatus.OPEN, newAccount.getAccountDetails().getAccountStatus());
        assertEquals("test installation account", newAccount.getAccountDetails().getName());
        assertEquals(OperatorType.INSTALLATION.name(), newAccount.getOperator().getType());
        assertEquals(true, newAccount.getTrustedAccountListRules().getRule1());
        assertEquals(true, newAccount.getTrustedAccountListRules().getRule2());

        List<InstallationOwnership> installationOwnerships = entityManager
            .createQuery(
                "SELECT i FROM InstallationOwnership i join fetch i.account join fetch i.installation where i.permitIdentifier in (:permitIdentifier)",
                InstallationOwnership.class)
            .setParameter("permitIdentifier", newAccount.getOperator().getPermit().getId())
            .getResultList();

        assertEquals(1L, installationOwnerships.size());
        var actualInstallationOwnership = installationOwnerships.get(0);
        assertEquals(newAccount.getOperator().getPermit().getId(), actualInstallationOwnership.getPermitIdentifier());
        assertEquals(InstallationOwnershipStatus.ACTIVE, actualInstallationOwnership.getStatus());
        assertEquals(newAccount.getIdentifier(), actualInstallationOwnership.getAccount().getIdentifier());
        assertEquals(newAccount.getOperator().getIdentifier(),
            actualInstallationOwnership.getInstallation().getIdentifier());


        List<AccountOwnership> accountOwnerShips = entityManager
            .createQuery(
                "SELECT ao FROM AccountOwnership ao join fetch ao.account join fetch ao.holder where ao.account.identifier in (:accountIdentifier)",
                AccountOwnership.class)
            .setParameter("accountIdentifier", newAccount.getIdentifier())
            .getResultList();

        assertEquals(1L, accountOwnerShips.size());
        assertEquals(AccountOwnershipStatus.ACTIVE, accountOwnerShips.get(0).getStatus());
    }

    @DisplayName("Open installation transfer account")
    @Test
    @Order(2)
    void testOpenAccountInstallationTransferAccount() {

        //given an installation exists
        AccountDTO existingInstallation = accountService.openAccount(this.readAccountFromJson("installation.json"));

        //given a new installation transfer request with the same identifier as an existing installation
        AccountDTO newInstallationTransferAccountDto = this.readAccountFromJson("installation-transfer.json");
        newInstallationTransferAccountDto.getOperator()
            .setIdentifier(existingInstallation.getOperator().getIdentifier());
        newInstallationTransferAccountDto.setInstallationToBeTransferred(existingInstallation.getOperator());

        //when
        newInstallationTransferAccountDto = accountService.openAccount(newInstallationTransferAccountDto);

        //then
        assertEquals(OperatorType.INSTALLATION.name(), newInstallationTransferAccountDto.getOperator().getType());
        assertEquals(OperatorType.INSTALLATION.name(), newInstallationTransferAccountDto.getOperator().getType());

        List<AccountOwnership> accountOwnerShips = entityManager
            .createQuery(
                "SELECT ao FROM AccountOwnership ao join fetch ao.account join fetch ao.holder where ao.account.identifier in (:accountIdentifier)",
                AccountOwnership.class)
            .setParameter("accountIdentifier", newInstallationTransferAccountDto.getIdentifier())
            .getResultList();

        assertEquals(1L, accountOwnerShips.size());
        assertEquals(AccountOwnershipStatus.ACTIVE, accountOwnerShips.get(0).getStatus());

        InstallationOwnership oldOwnerShip = entityManager
            .createQuery(
                "SELECT i FROM InstallationOwnership i join fetch i.account join fetch i.installation where i.account.identifier in (:accountIdentifier)",
                InstallationOwnership.class)
            .setParameter("accountIdentifier", existingInstallation.getIdentifier())
            .getSingleResult();


        assertEquals(InstallationOwnershipStatus.INACTIVE, oldOwnerShip.getStatus());


        InstallationOwnership newOwnership = entityManager
            .createQuery(
                "SELECT i FROM InstallationOwnership i join fetch i.account join fetch i.installation where i.account.identifier in (:accountIdentifier)",
                InstallationOwnership.class)
            .setParameter("accountIdentifier", newInstallationTransferAccountDto.getIdentifier())
            .getSingleResult();

//        assertEquals(2L, installationOwnerships.size());
        assertEquals(InstallationOwnershipStatus.ACTIVE, newOwnership.getStatus());

    }


//    @DisplayName("Open aircraft operator account")
//    @Order(4)
//    @Test
//    @Disabled("Will add one test in the refactoring of the account opening")
//    void testAircraftOperatorAccount() {
//
//    }

    private AccountDTO readAccountFromJson(String jsonFile) {
        Resource resource = resourceLoader.getResource(accountOpeningTestDataClassPath + jsonFile);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AccountDTO accountDTO;
        try {
            var installationAccount = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8.name());
            accountDTO = objectMapper.readValue(installationAccount, AccountDTO.class);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read resource");
        }
        // to avoid the error: This task cannot be approved as this emitter ID is used by another account
        accountDTO.getOperator().setEmitterId(UUID.randomUUID().toString());
        return accountDTO;
    }


}

