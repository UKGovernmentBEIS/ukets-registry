package gov.uk.ets.registry.api.account.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountFilter;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.shared.AccountProjection;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAircraftEntityToAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddInstallationEntityToAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddUserToAccountAccessCommand;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
class AccountSearchRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private AccountModelTestHelper helper;

    AddAccountHolderCommand addAccountHolderCommand;
    AddAccountCommand addAccountCommand;
    AddUserToAccountAccessCommand addAuthorizedRepresentativeToAccountCommand;
    AddInstallationEntityToAccountCommand addInstallationEntityToAccountCommand;

    @BeforeEach
    public void setUp() {
        helper = new AccountModelTestHelper(entityManager);
        addAccountHolderCommand = AddAccountHolderCommand.builder()
            .accountHolderType(AccountHolderType.GOVERNMENT)
            .identifier(100L)
            .name("Test account holder name")
            .firstName("  account  ")
            .lastName("   HOLDER  ")
            .status(Status.ACTIVE)
            .build();
        AccountHolder accountHolder = helper.addAccountHolder(addAccountHolderCommand);

        addAccountCommand = AddAccountCommand.builder()
            .accountHolder(accountHolder)
            .accountId(101L)
            .fullIdentifier("UK-100-123456-324")
            .accountName("Test-Account")
            .accountStatus(AccountStatus.OPEN)
            .complianceStatus(ComplianceStatus.A)
            .registryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)
            .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .build();
        Account account = helper.addAccount(addAccountCommand);

        addAuthorizedRepresentativeToAccountCommand =
            AddUserToAccountAccessCommand.builder()
                .account(account)
                .enrollmentKey("UK1234567")
                .state(AccountAccessState.ACTIVE)
                .urid("UK98567654")
                .build();
        helper.addUserToAccountAccess(addAuthorizedRepresentativeToAccountCommand);

        addInstallationEntityToAccountCommand = AddInstallationEntityToAccountCommand.builder()
            .account(account)
            .identifier(123L)
            .name("Test installation")
            .permitId("permit-id")
            .regulatorType(RegulatorType.EA)
            .build();
        helper.addInstallationToAccount(addInstallationEntityToAccountCommand);
    }

    @Test
    void test_search_with_account_full_identifier() {
        Pageable pageable = PageRequest.of(0,10);
        AccountFilter filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountFullIdentifierOrName(addAccountCommand.getFullIdentifier()).build();
        Page<AccountProjection> accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountFullIdentifierOrName(addAccountCommand.getFullIdentifier().substring(2, 6)).build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountFullIdentifierOrName(addAccountCommand.getFullIdentifier() + "other")
            .build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(0L, accounts.getTotalElements());
    }

    @Test
    void test_search_with_accountName() {
        Pageable pageable = PageRequest.of(0, 10);
        AccountFilter filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountFullIdentifierOrName(addAccountCommand.getAccountName()).build();
        Page<AccountProjection> accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountFullIdentifierOrName(addAccountCommand.getAccountName() + "more words").build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(0L, accounts.getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountFullIdentifierOrName(addAccountCommand.getAccountName()
            .substring(3, addAccountCommand.getAccountName().length() - 2))
            .build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());
    }

    @Test
    void test_search_with_accountStatuses() {
        Pageable pageable = PageRequest.of(0, 10);
        AccountFilter filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountStatuses(List.of(addAccountCommand.getAccountStatus())).build();
        Page<AccountProjection> accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        AccountProjection account = accounts.getContent().get(0);
        Account accountDb = getAccount(account.getIdentifier());
        accountDb.setAccountStatus(AccountStatus.OPEN);

        entityManager.persist(accountDb);
        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountStatuses(List.of(AccountStatus.ALL_TRANSACTIONS_RESTRICTED))
            .build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(0L, accounts.getTotalElements());
        accountDb = getAccount(account.getIdentifier());
        accountDb.setAccountStatus(addAccountCommand.getAccountStatus());

        entityManager.persist(accountDb);
    }

    @Test
    void test_search_with_account_holder_name() {
        Pageable pageable = PageRequest.of(0, 10);
        AccountFilter filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountHolderName(addAccountHolderCommand.getName()).build();
        Page<AccountProjection> accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountHolderName(addAccountHolderCommand.getName() + "more words").build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(0L, accounts.getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountHolderName(addAccountHolderCommand.getName()
            .substring(3, addAccountHolderCommand.getName().length() - 2))
            .build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        // test with first and last name
        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountHolderName("account holder").build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountHolderName("holder account").build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());
    }

    @Test
    void test_search_with_complianceStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        AccountFilter filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .complianceStatus(addAccountCommand.getComplianceStatus()).build();
        Page<AccountProjection> accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        AccountProjection account = accounts.getContent().get(0);
        Account accountDb = getAccount(account.getIdentifier());
        accountDb.setComplianceStatus(ComplianceStatus.EXCLUDED);
        entityManager.persist(accountDb);

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .complianceStatus(ComplianceStatus.A)
            .build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(0L, accounts.getTotalElements());

        account.setComplianceStatus(addAccountCommand.getComplianceStatus());
        accountDb = getAccount(account.getIdentifier());
        entityManager.persist(accountDb);
    }

    @Test
    void test_search_with_permitOrMonitoringPlanIdentifier() {
        Consumer<String> verifySuccessSearch = term -> {
            Pageable pageable = PageRequest.of(0,10);
            AccountFilter filter = AccountFilter
                .builder()
                .hasLimitedScope(true)
                .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
                .permitOrMonitoringPlanIdentifier(term).build();
            Page<AccountProjection> accounts = accountRepository.search(filter, pageable);
            assertEquals(1L, accounts.getTotalElements());
        };
        String term = addInstallationEntityToAccountCommand.getPermitId();
        verifySuccessSearch.accept(term);

        term = addInstallationEntityToAccountCommand.getPermitId()
            .substring(1, addInstallationEntityToAccountCommand.getPermitId().length() - 3);
        verifySuccessSearch.accept(term);

        AddAircraftEntityToAccountCommand addAircraftEntityToAccountCommand =
            AddAircraftEntityToAccountCommand.builder()
                .account(addInstallationEntityToAccountCommand.getAccount())
                .identifier(1234567L)
                .monitoringPlanId("monitoring-plan")
                .name("aircraft")
                .regulatorType(RegulatorType.DAERA)
                .build();
        helper.addAircraftToAccount(addAircraftEntityToAccountCommand);

        term = addAircraftEntityToAccountCommand.getMonitoringPlanId();
        verifySuccessSearch.accept(term);

        term = addAircraftEntityToAccountCommand.getMonitoringPlanId().substring(0, 4);
        verifySuccessSearch.accept(term);
    }

    @Test
    void test_search_with_authorized_representative_urid() {
        Pageable pageable = PageRequest.of(0,10);
        AccountFilter filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .build();
        Page<AccountProjection> accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid().substring(0, 3))
            .build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid() + "unexisted")
            .build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(0L, accounts.getTotalElements());

        AddUserToAccountAccessCommand addAnonAuthorizedRepresentativeCommand = AddUserToAccountAccessCommand
            .builder()
            .account(addAuthorizedRepresentativeToAccountCommand.getAccount())
            .enrollmentKey("UK090909")
            .urid("UK879856343")
            .build();

        helper.addUserToAccountAccess(addAnonAuthorizedRepresentativeCommand);
        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAnonAuthorizedRepresentativeCommand.getUrid())
            .build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());
    }

    @Test
    void test_search_with_account_type() {
        Pageable pageable = PageRequest.of(0, 10);
        AccountFilter filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountTypes(List.of(
                AccountType.get(addAccountCommand.getRegistryAccountType(), addAccountCommand.getKyotoAccountType())))
            .build();
        assertEquals(1L, accountRepository.search(filter, pageable).getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .accountTypes(List.of(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT))
            .build();
        assertEquals(0L, accountRepository.search(filter, pageable).getTotalElements());
    }

    @Test
    void test_search_with_regulator_type() {
        Pageable pageable = PageRequest.of(0,10);
        AccountFilter filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .regulatorType(addInstallationEntityToAccountCommand.getRegulatorType())
            .build();
        Page<AccountProjection> accounts = accountRepository.search(filter, pageable);
        assertEquals(1L, accounts.getTotalElements());

        filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .regulatorType(RegulatorType.DAERA).build();
        accounts = accountRepository.search(filter, pageable);
        assertEquals(0L, accounts.getTotalElements());
    }

    @Test
    void test_search_with_all_criteria() {
        Pageable pageable = PageRequest.of(0, 10);
        AccountFilter filter = AccountFilter
            .builder()
            .hasLimitedScope(true)
            .accountFullIdentifierOrName(addAccountCommand.getFullIdentifier())
            .accountStatuses(List.of(addAccountCommand.getAccountStatus()))
            .accountHolderName(addAccountHolderCommand.getName())
            .accountTypes(List.of(
                AccountType.get(addAccountCommand.getRegistryAccountType(), addAccountCommand.getKyotoAccountType())))
            .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
            .complianceStatus(addAccountCommand.getComplianceStatus())
            .permitOrMonitoringPlanIdentifier(addInstallationEntityToAccountCommand.getPermitId())
            .regulatorType(addInstallationEntityToAccountCommand.getRegulatorType())
            .build();
    Page<AccountProjection> accounts = accountRepository.search(filter, pageable);
    assertEquals(1L, accounts.getTotalElements());
  }

    @Test
    void test_search_with_InstallationOrAircraftOperatorId() {
        Consumer<String> verifySuccessSearch = term -> {
            Pageable pageable = PageRequest.of(0,10);
            AccountFilter filter = AccountFilter
                .builder()
                .hasLimitedScope(true)
                .authorizedRepresentativeUrid(addAuthorizedRepresentativeToAccountCommand.getUrid())
                .installationOrAircraftOperatorId(term).build();
            Page<AccountProjection> accounts = accountRepository.search(filter, pageable);
            assertEquals(1L, accounts.getTotalElements());
        };

        String term = addInstallationEntityToAccountCommand.getIdentifier().toString();
        verifySuccessSearch.accept(term);

        AddAircraftEntityToAccountCommand addAircraftEntityToAccountCommand =
            AddAircraftEntityToAccountCommand.builder()
                .account(addInstallationEntityToAccountCommand.getAccount())
                .identifier(1234567L)
                .monitoringPlanId("monitoring-plan")
                .name("aircraft")
                .regulatorType(RegulatorType.DAERA)
                .build();
        helper.addAircraftToAccount(addAircraftEntityToAccountCommand);
        term = addAircraftEntityToAccountCommand.getIdentifier().toString();
        verifySuccessSearch.accept(term);
    }

    private Account getAccount(Long accountIdentifier) {
        Optional<Account> accountOptional = accountRepository.findByIdentifier(accountIdentifier);
        return accountOptional.orElse(null);
    }
}
