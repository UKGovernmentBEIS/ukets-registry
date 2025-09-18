package gov.uk.ets.registry.api.account.repository;

import static gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;
import static gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.InstallationOwnership;
import gov.uk.ets.registry.api.account.domain.InstallationOwnershipStatus;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create"
})
public class InstallationOwnershipRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private InstallationOwnershipRepository installationOwnershipRepository;

    private Installation installation;

    @BeforeEach
    void setUp() {
        AccountModelTestHelper accountHelper = new AccountModelTestHelper(entityManager);
        AddAccountHolderCommand addAccountHolderCommand = AddAccountHolderCommand.builder()
                                                         .accountHolderType(AccountHolderType.ORGANISATION)
                                                         .identifier(100001L)
                                                         .name("Test account holder name")
                                                         .status(Status.ACTIVE)
                                                         .build();
        AccountHolder accountHolder = accountHelper.addAccountHolder(addAccountHolderCommand);

        AddAccountCommand addAccountCommand = AddAccountCommand.builder()
                                                               .accountHolder(accountHolder)
                                                               .accountId(10001L)
                                                               .fullIdentifier("UK-100-10001-325")
                                                               .accountName("Test-Account-1")
                                                               .accountStatus(AccountStatus.OPEN)
                                                               .build();
        Account account1 = accountHelper.addAccount(addAccountCommand);

        addAccountCommand = AddAccountCommand.builder()
                                             .accountHolder(accountHolder)
                                             .accountId(10002L)
                                             .fullIdentifier("UK-100-10002-326")
                                             .accountName("Test-Account-2")
                                             .accountStatus(AccountStatus.OPEN)
                                             .build();
        Account account2 = accountHelper.addAccount(addAccountCommand);

        addAccountCommand = AddAccountCommand.builder()
                                             .accountHolder(accountHolder)
                                             .accountId(10003L)
                                             .fullIdentifier("UK-100-10003-327")
                                             .accountName("Test-Account-3")
                                             .accountStatus(AccountStatus.OPEN)
                                             .build();
        Account account3 = accountHelper.addAccount(addAccountCommand);

        installation = new Installation();
        installation.setInstallationName("Test installation");
        installation.setStartYear(2016);
        installation.setEndYear(2020);
        installation.setIdentifier(123L);
        installation.setRegulator(RegulatorType.EA);
        installation.setPermitIdentifier("permit-id");
        entityManager.persist(installation);

        createInstallationOwnershipEntries(account1, installation, InstallationOwnershipStatus.ACTIVE, LocalDate.now());
        createInstallationOwnershipEntries(account2, installation, InstallationOwnershipStatus.INACTIVE, LocalDate.now().minusDays(1L));
        createInstallationOwnershipEntries(account3, installation, InstallationOwnershipStatus.INACTIVE, LocalDate.now().minusDays(2L));
    }

    @DisplayName("Installation ownership query executes findByAccountIdentifierAndStatus with success.")
    @Test
    void test_findByAccountIdentifierAndStatus() {

        List<InstallationOwnership> installationOwnerships =
            installationOwnershipRepository.findByAccountIdentifierAndStatus(10001L,
                                                                             InstallationOwnershipStatus.ACTIVE);
        assertEquals(1, installationOwnerships.size());
    }

    @DisplayName("Installation ownership query executes findByInstallationAndStatusOrderByOwnershipDateDesc with success.")
    @Test
    void test_findByInstallationAndStatusOrderByOwnershipDateDesc() {

        List<InstallationOwnership> installationOwnerships =
            installationOwnershipRepository
                .findByInstallationAndStatusOrderByOwnershipDateDesc(installation, InstallationOwnershipStatus.INACTIVE);
        assertEquals("Test-Account-2", installationOwnerships.get(0).getAccount().getAccountName());
    }

    @DisplayName("Installation ownership query executes findByInstallation with success.")
    @Test
    void test_findByInstallation() {

        List<InstallationOwnership> installationOwnerships =
            installationOwnershipRepository
                .findByInstallation(installation);
        assertEquals(3, installationOwnerships.size());
    }
    
    void createInstallationOwnershipEntries(Account account, Installation installation,
                                            InstallationOwnershipStatus status, LocalDate date) {
        InstallationOwnership installationOwnership = new InstallationOwnership();
        installationOwnership.setAccount(account);
        installationOwnership.setInstallation(installation);
        installationOwnership.setStatus(status);
        installationOwnership.setOwnershipDate(Date.valueOf(date));
        entityManager.persist(installationOwnership);
    }
}
