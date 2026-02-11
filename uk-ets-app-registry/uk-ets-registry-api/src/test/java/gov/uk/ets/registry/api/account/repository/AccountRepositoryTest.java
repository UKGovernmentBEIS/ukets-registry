package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
public class AccountRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;
    
    @DisplayName("Count the number of Installations with the provided PermitId belong to accounts that are neither Closed nor Rejected .")
    @ParameterizedTest(name = "#{index} - Account with status {0} should count.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "PROPOSED"})
    void countInstallationsByPermitIdForNonClosedOrNonRejectedAccounts(AccountStatus status) {
        
        String permitId = "12345".toUpperCase();

        Installation installation = getInstallation(permitId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);
        entityManager.persist(account);
        entityManager.persist(installation);
        
        Long result = accountRepository.countInstallationsByPermitIdForNonClosedOrRejectedAccounts(permitId);
        assertSame(1L,result);
    } 
    
    @DisplayName("Count the number of Installations with the provided PermitId belong to accounts that are Closed Or Rejected .")
    @ParameterizedTest(name = "#{index} - Account with status {0} should not count.")
    @EnumSource(value = AccountStatus.class, names = {"CLOSED", "REJECTED"})
    void countInstallationsByPermitIdForClosedOrRejectedAccounts(AccountStatus status) {
        
        String permitId = "12345".toUpperCase();

        Installation installation = getInstallation(permitId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);
        
        entityManager.persist(installation);
        entityManager.persist(account);
        
        Long result = accountRepository.countInstallationsByPermitIdForNonClosedOrRejectedAccounts(permitId);
        assertSame(0L,result);
    }

    @ParameterizedTest(name = "#{index} - Emitter Id exists for MaritimeOperator Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void checkIsExistingEmitterIdForNotClosedMaritimeOperatorAccount(AccountStatus status){
        // get an operator Account
        String emitterId = "RTUIOLKVSOIV6877";
        Long operatorId = 100024L;
        String permitId = "12345".toUpperCase();
        MaritimeOperator maritime = getMaritimeOperator(operatorId,emitterId);
        // set its status and emitterId
        Account maritimeAccount = getOperatorHoldingAccount(operatorId,RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                                                    AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,status);
        maritimeAccount.setCompliantEntity(maritime);
        entityManager.persist(maritime);
        entityManager.persist(maritimeAccount);

        Installation installation = getInstallation(permitId);
        installation.setEmitterId(emitterId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        // check if its present
        boolean exists = accountRepository.isExistingEmitterId(operatorId,emitterId,AccountStatus.CLOSED);
        // assert true
        assertTrue(exists);
    }

    @ParameterizedTest(name = "#{index} - Emitter Id exists for AircraftOperator Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void checkIsExistingEmitterIdForNotClosedAircraftOperatorAccount(AccountStatus status){
        // get an operator Account
        String emitterId = "RTUIOLKVSOIV6877";
        Long operatorId = 100024L;
        String permitId = "12345".toUpperCase();

        AircraftOperator aircraft = getAircraftOperator(operatorId,emitterId);
        // set its status and emitterId
        Account aircraftAccount = getOperatorHoldingAccount(operatorId,RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,status);
        aircraftAccount.setCompliantEntity(aircraft);
        entityManager.persist(aircraft);
        entityManager.persist(aircraftAccount);

        Installation installation = getInstallation(permitId);
        installation.setEmitterId(emitterId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        // check if its present
        boolean exists = accountRepository.isExistingEmitterId(operatorId,emitterId,AccountStatus.CLOSED);
        // assert true
        assertTrue(exists);
    }

    @ParameterizedTest(name = "#{index} - Emitter Id exists for Installation Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void checkIsExistingEmitterIdForNotClosedInstallationOperatorAccount(AccountStatus status){
        // get an operator Account
        String emitterId = "RTUIOLKVSOIV6877";
        Long operatorId = 100024L;
        String permitId = "12345".toUpperCase();

        Installation installation = getInstallation(permitId);
        installation.setEmitterId(emitterId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        MaritimeOperator maritime = getMaritimeOperator(operatorId,emitterId);
        // set its status and emitterId
        Account maritimeAccount = getOperatorHoldingAccount(operatorId,RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,status);
        maritimeAccount.setCompliantEntity(maritime);
        entityManager.persist(maritime);
        entityManager.persist(maritimeAccount);

        // check if its present
        boolean exists = accountRepository.isExistingEmitterId(operatorId,emitterId,AccountStatus.CLOSED);
        // assert true
        assertTrue(exists);
    }

    @ParameterizedTest(name = "#{index} - isExistingEmitterId returns false if it exists on a MaritimeOperator Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"CLOSED"})
    void checkIsNotExistingEmitterIdForClosedMaritimeOperatorAccount(AccountStatus status){
        // get an operator Account
        String emitterId = "RTUIOLKVSOIV6877";
        Long operatorId = 100024L;
        String permitId = "12345".toUpperCase();
        MaritimeOperator maritime = getMaritimeOperator(operatorId,emitterId);
        // set its status and emitterId
        Account maritimeAccount = getOperatorHoldingAccount(operatorId,RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,status);
        maritimeAccount.setCompliantEntity(maritime);
        entityManager.persist(maritime);
        entityManager.persist(maritimeAccount);

        Installation installation = getInstallation(permitId);
        installation.setEmitterId(emitterId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        // check if its present
        boolean exists = accountRepository.isExistingEmitterId(operatorId,emitterId,AccountStatus.CLOSED);
        // assert true
        assertFalse(exists);
    }

    @ParameterizedTest(name = "#{index} - isExistingEmitterId returns false if it exists on a AircraftOperator Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"CLOSED"})
    void checkIsNotExistingEmitterIdForClosedAircraftOperatorAccount(AccountStatus status){
        // get an operator Account
        String emitterId = "RTUIOLKVSOIV6877";
        Long operatorId = 100024L;
        String permitId = "12345".toUpperCase();

        AircraftOperator aircraft = getAircraftOperator(operatorId,emitterId);
        // set its status and emitterId
        Account aircraftAccount = getOperatorHoldingAccount(operatorId,RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,status);
        aircraftAccount.setCompliantEntity(aircraft);
        entityManager.persist(aircraft);
        entityManager.persist(aircraftAccount);

        Installation installation = getInstallation(permitId);
        installation.setEmitterId(emitterId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        // check if its present
        boolean exists = accountRepository.isExistingEmitterId(operatorId,emitterId,AccountStatus.CLOSED);
        // assert true
        assertFalse(exists);
    }

    @ParameterizedTest(name = "#{index} - isExistingEmitterId returns false if it exists on a Installation Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"CLOSED"})
    void checkIsNotExistingEmitterIdForClosedInstallationOperatorAccount(AccountStatus status){
        // get an operator Account
        String emitterId = "RTUIOLKVSOIV6877";
        Long operatorId = 100024L;
        String permitId = "12345".toUpperCase();

        Installation installation = getInstallation(permitId);
        installation.setEmitterId(emitterId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        MaritimeOperator maritime = getMaritimeOperator(operatorId,emitterId);
        // set its status and emitterId
        Account maritimeAccount = getOperatorHoldingAccount(operatorId,RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,status);
        maritimeAccount.setCompliantEntity(maritime);
        entityManager.persist(maritime);
        entityManager.persist(maritimeAccount);

        // check if its present
        boolean exists = accountRepository.isExistingEmitterId(operatorId,emitterId,AccountStatus.CLOSED);
        // assert true
        assertFalse(exists);
    }

    @ParameterizedTest(name = "#{index} - Emitter Id DOES NOT exist for Maritime Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void checkIsNotExistingEmitterIdForNotClosedMaritimeOperatorAccount(AccountStatus status){
        // get an operator Account
        String emitterId = "RTUIOLKVSOIV6877";
        Long operatorId = 100024L;
        String permitId = "12345".toUpperCase();

        Installation installation = getInstallation(permitId);
        installation.setEmitterId("DIFFERENT-EM-ID");
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        account.setIdentifier(1111L);
        installation.setIdentifier(1111L);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        MaritimeOperator maritime = getMaritimeOperator(operatorId,emitterId);
        // set its status and emitterId
        Account maritimeAccount = getOperatorHoldingAccount(operatorId,RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,status);
        maritimeAccount.setCompliantEntity(maritime);
        entityManager.persist(maritime);
        entityManager.persist(maritimeAccount);

        // check if its present
        boolean exists = accountRepository.isExistingEmitterId(operatorId,emitterId,AccountStatus.CLOSED);
        // assert true
        assertFalse(exists);
    }

    @ParameterizedTest(name = "#{index} - Emitter Id DOES NOT exist for Aircraft Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void checkIsNotExistingEmitterIdForNotClosedAircraftOperatorAccount(AccountStatus status){
        // get an operator Account
        String emitterId = "RTUIOLKVSOIV6877";
        Long operatorId = 100024L;
        String permitId = "12345".toUpperCase();

        Installation installation = getInstallation(permitId);
        installation.setEmitterId("DIFFERENT-EM-ID");
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        account.setIdentifier(1111L);
        installation.setIdentifier(1111L);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        AircraftOperator aircraft = getAircraftOperator(operatorId,emitterId);
        // set its status and emitterId
        Account aircraftAccount = getOperatorHoldingAccount(operatorId,RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,status);
        aircraftAccount.setCompliantEntity(aircraft);
        entityManager.persist(aircraft);
        entityManager.persist(aircraftAccount);

        // check if its present
        boolean exists = accountRepository.isExistingEmitterId(operatorId,emitterId,AccountStatus.CLOSED);
        // assert true
        assertFalse(exists);
    }

    @ParameterizedTest(name = "#{index} - Emitter Id does NOT exist for Installation Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void checkIsNotExistingEmitterIdForNotClosedInstallationOperatorAccount(AccountStatus status){
        // get an operator Account
        String emitterId = "RTUIOLKVSOIV6877";
        Long operatorId = 100024L;
        String permitId = "12345".toUpperCase();

        Installation installation = getInstallation(permitId);
        installation.setEmitterId("DIFFERENT-EM-ID");
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        account.setIdentifier(1111L);
        installation.setIdentifier(1111L);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        MaritimeOperator maritime = getMaritimeOperator(operatorId,emitterId);
        // set its status and emitterId
        Account maritimeAccount = getOperatorHoldingAccount(operatorId,RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,status);
        maritimeAccount.setCompliantEntity(maritime);
        entityManager.persist(maritime);
        entityManager.persist(maritimeAccount);

        // check if its present
        boolean exists = accountRepository.isExistingEmitterId(operatorId,emitterId,AccountStatus.CLOSED);
        // assert true
        assertFalse(exists);
    }

    @ParameterizedTest(name = "Finds an account by operator Id and account claim code.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void findByCompliantEntityIdentifierAndAccountClaimCode(AccountStatus status){
        // get an operator Account
        final String emitterId = "RTUIOLKVSOIV6877";
        final Long operatorId = 1111L;
        final String permitId = "12345".toUpperCase();
        final String accountClaimCode = "ACC123456789";

        Installation installation = getInstallation(permitId);
        installation.setEmitterId(emitterId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        final Optional<Account> actual = accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(operatorId, accountClaimCode);
        assertTrue(actual.isPresent());
        assertEquals(account, actual.get());
    }


    @Test
    void findByCompliantEntityIdentifierAndAccountClaimCode_no_result(){

        final Long operatorId = 1111L;
        final String accountClaimCode = "ACC123456789";

        final Optional<Account> actual = accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(operatorId, accountClaimCode);
        assertTrue(actual.isEmpty());
    }

    private Account getOperatorHoldingAccount(AccountStatus status) {
        Account account = new Account();
        account.setAccountName("An " + status +  " Account");
        account.setAccountStatus(status);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        final AccountType kyotoType = AccountType.PERSON_HOLDING_ACCOUNT;
        account.setKyotoAccountType(kyotoType.getKyotoType());
        account.setRegistryAccountType(kyotoType.getRegistryType());
        account.setRegistryCode(kyotoType.getRegistryCode());
        account.setBillingAddressSameAsAccountHolderAddress(true);
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setIdentifier(1222L);
        account.setCommitmentPeriodCode(2);
        account.setCheckDigits(44);
        account.setFullIdentifier(String.format("%s-%d-%d-%d-%d", kyotoType.getRegistryCode(), kyotoType.getKyotoCode(), account.getIdentifier(), account.getCommitmentPeriodCode(), account.getCheckDigits()));
        account.setAccountClaimCode("ACC123456789");

        return account;
    }

    @ParameterizedTest(name = "Finds an account by account claim code is null.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void findByAccountClaimCodeIsNull_no_results(AccountStatus status){
        // get an operator Account
        final String emitterId = "RTUIOLKVSOIV6877";
        final String permitId = "12345".toUpperCase();


        Installation installation = getInstallation(permitId);
        installation.setEmitterId(emitterId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        final List<Account> actual = accountRepository.findByAccountClaimCodeIsNull();
        assertTrue(actual.isEmpty());
    }

    @ParameterizedTest(name = "Finds an account by account claim code is null.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void findByAccountClaimCodeIsNull(AccountStatus status){
        // get an operator Account
        final String emitterId = "RTUIOLKVSOIV6877";
        final String permitId = "12345".toUpperCase();


        Installation installation = getInstallation(permitId);
        installation.setEmitterId(emitterId);
        Account account = getOperatorHoldingAccount(status);
        account.setCompliantEntity(installation);
        account.setAccountClaimCode(null);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        final List<Account> actual = accountRepository.findByAccountClaimCodeIsNull();
        assertThat(actual).hasSize(1);
        assertNull(actual.get(0).getAccountClaimCode());
    }

    @ParameterizedTest(name = "#{index} - Emitter Id exists for MaritimeOperator Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void findByRegistryAccountTypeIn(AccountStatus status) {

        String emitterId = "RTUIOLKVSOIV6877";
        Long maritimeOperatorId = 100024L;
        Long aircraftOperatorId = 100025L;
        Long operatorId = 100026L;
        String permitId = "12345".toUpperCase();
        MaritimeOperator maritime = getMaritimeOperator(maritimeOperatorId,emitterId);

        Account maritimeAccount = getOperatorHoldingAccount(maritimeOperatorId,RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,status);
        maritimeAccount.setCompliantEntity(maritime);
        maritime.setAccount(maritimeAccount);
        entityManager.persist(maritime);
        entityManager.persist(maritimeAccount);

        AircraftOperator aircraftOperator = getAircraftOperator(aircraftOperatorId,emitterId);

        Account aircraftAccount = getOperatorHoldingAccount(aircraftOperatorId,RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,status);
        aircraftAccount.setCompliantEntity(aircraftOperator);
        aircraftOperator.setAccount(aircraftAccount);
        entityManager.persist(aircraftOperator);
        entityManager.persist(aircraftAccount);

        Installation installation = getInstallation(permitId);
        installation.setEmitterId(emitterId);
        Account account = getOperatorHoldingAccount(operatorId, RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
                AccountType.OPERATOR_HOLDING_ACCOUNT,status);
        account.setCompliantEntity(installation);
        installation.setAccount(account);

        entityManager.persist(installation);
        entityManager.persist(account);

        final List<Account> accounts = accountRepository.findByRegistryAccountTypeIn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT, RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                RegistryAccountType.TRADING_ACCOUNT));

        assertThat(accounts).hasSize(3);
        assertThat(accounts).extracting(Account::getRegistryAccountType).containsExactlyInAnyOrder(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT, RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT);
    }

    @ParameterizedTest(name = "#{index} - Emitter Id exists for MaritimeOperator Account with status {0}.")
    @EnumSource(value = AccountStatus.class, names = {"OPEN", "ALL_TRANSACTIONS_RESTRICTED", "SOME_TRANSACTIONS_RESTRICTED", "SUSPENDED_PARTIALLY", "SUSPENDED", "TRANSFER_PENDING", "CLOSURE_PENDING", "REJECTED", "PROPOSED"})
    void findByRegistryAccountTypeIn_no_results(AccountStatus status) {

        String emitterId = "RTUIOLKVSOIV6877";
        Long maritimeOperatorId = 100024L;
        Long aircraftOperatorId = 100025L;
        MaritimeOperator maritime = getMaritimeOperator(maritimeOperatorId,emitterId);

        Account maritimeAccount = getOperatorHoldingAccount(maritimeOperatorId,RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,status);
        maritimeAccount.setCompliantEntity(maritime);
        maritime.setAccount(maritimeAccount);
        entityManager.persist(maritime);
        entityManager.persist(maritimeAccount);

        AircraftOperator aircraftOperator = getAircraftOperator(aircraftOperatorId,emitterId);

        Account aircraftAccount = getOperatorHoldingAccount(aircraftOperatorId,RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,status);
        aircraftAccount.setCompliantEntity(aircraftOperator);
        aircraftOperator.setAccount(aircraftAccount);
        entityManager.persist(aircraftOperator);
        entityManager.persist(aircraftAccount);

        final List<Account> accounts = accountRepository.findByRegistryAccountTypeIn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
                RegistryAccountType.TRADING_ACCOUNT));

        assertThat(accounts).isEmpty();
    }

    private Installation getInstallation(String permitId) {
        Installation installation = new Installation();
        installation.setChangedRegulator(RegulatorType.OPRED);
        installation.setIdentifier(1111L);
        installation.setInstallationName("An installation name.");
        installation.setPermitIdentifier(permitId);
        installation.setStatus(Status.REJECTED);
        installation.setStartYear(2021);
        return installation;
    }

    private Account getOperatorHoldingAccount(Long operatorId,RegistryAccountType registryAccountType,AccountType kyotoType,AccountStatus status) {
        Account account = new Account();
        account.setAccountName("An " + status +  " Account");
        account.setAccountStatus(status);
        account.setRegistryAccountType(registryAccountType);
        account.setKyotoAccountType(kyotoType.getKyotoType());
        account.setRegistryAccountType(kyotoType.getRegistryType());
        account.setRegistryCode(kyotoType.getRegistryCode());
        account.setBillingAddressSameAsAccountHolderAddress(true);
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setIdentifier(operatorId);
        account.setCommitmentPeriodCode(2);
        account.setCheckDigits(44);
        account.setFullIdentifier(String.format("%s-%d-%d-%d-%d", kyotoType.getRegistryCode(), kyotoType.getKyotoCode(), account.getIdentifier(), account.getCommitmentPeriodCode(), account.getCheckDigits()));

        return account;
    }

    private MaritimeOperator getMaritimeOperator(Long OperatorId,String emitterId) {
        MaritimeOperator maritime = new MaritimeOperator();
        maritime.setChangedRegulator(RegulatorType.OPRED);
        maritime.setIdentifier(OperatorId);
        maritime.setStatus(Status.REJECTED);
        maritime.setStartYear(2021);
        maritime.setMaritimeMonitoringPlanIdentifier("RTHS568JY-O");
        maritime.setEmitterId(emitterId);
        return maritime;
    }
    private AircraftOperator getAircraftOperator(Long OperatorId,String emitterId) {
        AircraftOperator aircraft = new AircraftOperator();
        aircraft.setChangedRegulator(RegulatorType.OPRED);
        aircraft.setIdentifier(OperatorId);
        aircraft.setStatus(Status.REJECTED);
        aircraft.setMonitoringPlanIdentifier("RTHS568JY-O");
        aircraft.setEmitterId(emitterId);
        aircraft.setStartYear(2021);
        return aircraft;
    }


}
