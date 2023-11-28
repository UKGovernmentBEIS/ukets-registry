package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.PermitStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
        
        return account;
    }
    
    private Installation getInstallation(String permitId) {
        Installation installation = new Installation();
        installation.setActivityType("An activity");
        installation.setChangedRegulator(RegulatorType.OPRED);
        installation.setIdentifier(1111L);
        installation.setInstallationName("An installation name.");
        installation.setPermitIdentifier(permitId);
        installation.setStatus(Status.REJECTED);
        installation.setPermitStatus(PermitStatus.ACTIVE);
        installation.setStartYear(2021);

        return installation;
    }
}
