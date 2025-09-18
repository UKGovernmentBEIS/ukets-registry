package gov.uk.ets.registry.api.tal.repository;

import static gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import gov.uk.ets.registry.api.tal.TrustedAccountTestHelper;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
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
class TrustedAccountRepositoryTest {
    AddAccountHolderCommand addAccountHolderCommand;
    AddAccountCommand addAccountCommand;
    TrustedAccountTestHelper.AddTrustedAccountCommand addTrustedAccountCommand;
    @PersistenceContext
    private EntityManager entityManager;
    private AccountModelTestHelper accountHelper;
    private TrustedAccountTestHelper trustedAccountHelper;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TrustedAccountRepository repository;

    private AccountHolder accountHolder;
    private Account account;

    @BeforeEach
    public void setUp() {
        accountHelper = new AccountModelTestHelper(entityManager);
        addAccountHolderCommand = AddAccountHolderCommand.builder()
            .accountHolderType(AccountHolderType.ORGANISATION)
            .identifier(10001L)
            .name("Test account holder name")
            .status(Status.ACTIVE)
            .build();
        accountHolder = accountHelper.addAccountHolder(addAccountHolderCommand);

        addAccountCommand = AddAccountCommand.builder()
            .accountHolder(accountHolder)
            .accountId(10001L)
            .fullIdentifier("UK-100-10001-324")
            .accountName("Test-Account-1")
            .accountStatus(AccountStatus.OPEN)
            .build();
        account = accountHelper.addAccount(addAccountCommand);

        trustedAccountHelper = new TrustedAccountTestHelper(entityManager);
    }

    @DisplayName("Test trusted accounts under same account holder")
    @Test
    void testTrustedAccountsUnderSameAccountHolder() {
        addAccountCommand = AddAccountCommand.builder()
            .accountHolder(accountHolder)
            .accountId(10002L)
            .fullIdentifier("UK-100-10002-123")
            .accountName("Test-Account-2")
            .accountStatus(AccountStatus.OPEN)
            .build();
        Account trustedAccount1 = accountHelper.addAccount(addAccountCommand);
        addAccountCommand = AddAccountCommand.builder()
            .accountHolder(accountHolder)
            .accountId(10003L)
            .fullIdentifier("UK-100-10003-123")
            .accountName("Test-Account-3")
            .accountStatus(AccountStatus.OPEN)
            .build();
        Account trustedAccount2 = accountHelper.addAccount(addAccountCommand);
        addAccountCommand = AddAccountCommand.builder()
            .accountHolder(accountHolder)
            .accountId(10004L)
            .fullIdentifier("UK-100-10004-123")
            .accountName("Test-Account-4")
            .accountStatus(AccountStatus.CLOSED)
            .build();
        Account trustedAccount3 = accountHelper.addAccount(addAccountCommand);
        addAccountHolderCommand = AddAccountHolderCommand.builder()
            .accountHolderType(AccountHolderType.ORGANISATION)
            .identifier(10002L)
            .name("Test account holder name 2")
            .status(Status.ACTIVE)
            .build();
        AccountHolder otherAccountHolder = accountHelper.addAccountHolder(addAccountHolderCommand);
        addAccountCommand = AddAccountCommand.builder()
            .accountHolder(otherAccountHolder)
            .accountId(10005L)
            .fullIdentifier("UK-100-10005-123")
            .accountName("Test-Account-5")
            .accountStatus(AccountStatus.OPEN)
            .build();
        Account notTrustedAccount = accountHelper.addAccount(addAccountCommand);

        List<Account> trustedAccounts = accountRepository.findAccountsOfTheSameAccountHolder(account.getIdentifier());
        Assertions.assertTrue(trustedAccounts.stream()
            .anyMatch(ta -> ta.getFullIdentifier().equals(trustedAccount1.getFullIdentifier())));
        Assertions.assertTrue(trustedAccounts.stream()
            .anyMatch(ta -> ta.getFullIdentifier().equals(trustedAccount2.getFullIdentifier())));
        Assertions.assertFalse(trustedAccounts.stream()
            .anyMatch(ta -> ta.getFullIdentifier().equals(trustedAccount3.getFullIdentifier())));
        Assertions.assertFalse(trustedAccounts.stream()
            .anyMatch(ta -> ta.getFullIdentifier().equals(notTrustedAccount.getFullIdentifier())));
    }

    @DisplayName("Test trusted accounts that are not under the same account holder")
    @Test
    void testOtherTrustedAccounts() {
        addTrustedAccountCommand = TrustedAccountTestHelper.AddTrustedAccountCommand.builder()
            .account(account)
            .trustedAccountFullIdentifier("JP-100-12345-678")
            .description("Description of the external trusted account")
            .status(TrustedAccountStatus.ACTIVE)
            .build();
        TrustedAccount trustedAccount1 = trustedAccountHelper.addTrustedAccount(addTrustedAccountCommand);
        addTrustedAccountCommand = TrustedAccountTestHelper.AddTrustedAccountCommand.builder()
            .account(account)
            .trustedAccountFullIdentifier("UK-100-12312-767")
            .description("Description of the active registry trusted account")
            .status(TrustedAccountStatus.PENDING_ACTIVATION)
            .build();
        TrustedAccount trustedAccount2 = trustedAccountHelper.addTrustedAccount(addTrustedAccountCommand);
        addTrustedAccountCommand = TrustedAccountTestHelper.AddTrustedAccountCommand.builder()
            .account(account)
            .trustedAccountFullIdentifier("UK-100-324523-767")
            .description("Description of the pending approval registry trusted account")
            .status(TrustedAccountStatus.PENDING_ADDITION_APPROVAL)
            .build();
        TrustedAccount trustedAccount3 = trustedAccountHelper.addTrustedAccount(addTrustedAccountCommand);

        addAccountCommand = AddAccountCommand.builder()
            .accountHolder(accountHolder)
            .accountId(10002L)
            .fullIdentifier("UK-100-10002-123")
            .accountName("Test-Account-2")
            .accountStatus(AccountStatus.OPEN)
            .build();
        Account otherAccount = accountHelper.addAccount(addAccountCommand);
        addTrustedAccountCommand = TrustedAccountTestHelper.AddTrustedAccountCommand.builder()
            .account(otherAccount)
            .trustedAccountFullIdentifier("UK-100-56671-895")
            .description("Description of a trusted account of another account")
            .status(TrustedAccountStatus.ACTIVE)
            .build();
        TrustedAccount notTrustedAccount = trustedAccountHelper.addTrustedAccount(addTrustedAccountCommand);

        List<TrustedAccount> trustedAccounts = repository.findAllByAccountIdentifier(account.getIdentifier());
        Assertions.assertTrue(trustedAccounts.stream()
            .anyMatch(
                ta -> ta.getTrustedAccountFullIdentifier().equals(trustedAccount1.getTrustedAccountFullIdentifier())));
        Assertions.assertTrue(trustedAccounts.stream()
            .anyMatch(
                ta -> ta.getTrustedAccountFullIdentifier().equals(trustedAccount2.getTrustedAccountFullIdentifier())));
        Assertions.assertTrue(trustedAccounts.stream()
            .anyMatch(
                ta -> ta.getTrustedAccountFullIdentifier().equals(trustedAccount3.getTrustedAccountFullIdentifier())));
        Assertions.assertFalse(trustedAccounts.stream()
            .anyMatch(ta -> ta.getTrustedAccountFullIdentifier()
                .equals(notTrustedAccount.getTrustedAccountFullIdentifier())));

        List<TrustedAccount> trustedAccountsApprovedOrActive =
            repository.findAllByAccountIdentifierAndStatusIn(account.getIdentifier(),
                Arrays.asList(TrustedAccountStatus.ACTIVE, TrustedAccountStatus.PENDING_ACTIVATION));
        Assertions.assertTrue(trustedAccountsApprovedOrActive.stream()
            .anyMatch(
                ta -> ta.getTrustedAccountFullIdentifier().equals(trustedAccount1.getTrustedAccountFullIdentifier())));
        Assertions.assertTrue(trustedAccountsApprovedOrActive.stream()
            .anyMatch(
                ta -> ta.getTrustedAccountFullIdentifier().equals(trustedAccount2.getTrustedAccountFullIdentifier())));
        Assertions.assertFalse(trustedAccountsApprovedOrActive.stream()
            .anyMatch(ta -> ta.getTrustedAccountFullIdentifier()
                .equals(trustedAccount3.getTrustedAccountFullIdentifier())));
        Assertions.assertFalse(trustedAccountsApprovedOrActive.stream()
            .anyMatch(ta -> ta.getTrustedAccountFullIdentifier()
                .equals(notTrustedAccount.getTrustedAccountFullIdentifier())));

        List<TrustedAccount> trustedAccountsPendingApproval =
            repository.findAllByAccountIdentifierAndStatusIn(account.getIdentifier(),
                Collections.singletonList(TrustedAccountStatus.PENDING_ADDITION_APPROVAL));
        Assertions.assertFalse(trustedAccountsPendingApproval.stream()
            .anyMatch(
                ta -> ta.getTrustedAccountFullIdentifier().equals(trustedAccount1.getTrustedAccountFullIdentifier())));
        Assertions.assertFalse(trustedAccountsPendingApproval.stream()
            .anyMatch(
                ta -> ta.getTrustedAccountFullIdentifier().equals(trustedAccount2.getTrustedAccountFullIdentifier())));
        Assertions.assertTrue(trustedAccountsPendingApproval.stream()
            .anyMatch(ta -> ta.getTrustedAccountFullIdentifier()
                .equals(trustedAccount3.getTrustedAccountFullIdentifier())));
        Assertions.assertFalse(trustedAccountsPendingApproval.stream()
            .anyMatch(ta -> ta.getTrustedAccountFullIdentifier()
                .equals(notTrustedAccount.getTrustedAccountFullIdentifier())));
    }
}
