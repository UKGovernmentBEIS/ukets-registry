package gov.uk.ets.registry.api.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddUserToAccountAccessCommand;
import gov.uk.ets.registry.api.helper.persistence.TaskModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.TaskModelTestHelper.AddTaskCommand;
import gov.uk.ets.registry.api.helper.persistence.TaskModelTestHelper.AddUserCommand;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper.AddAccountInfoCommand;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper.AddTransactionCommand;
import gov.uk.ets.registry.api.transaction.domain.BaseTransactionEntity;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilter;
import gov.uk.ets.registry.api.transaction.domain.TransactionProjection;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.SearchableTransactionRepository;
import gov.uk.ets.registry.api.user.domain.User;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;


@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create"
})
class TransactionProjectionRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SearchableTransactionRepository repository;

    private AccountModelTestHelper accountModelTestHelper;
    private AddAccountCommand addAcquiringAccountCommand;
    private AddTransactionCommand addTransactionCommand;
    private AddUserCommand addInitiatorCommand;

    private Account transferringAccount;
    private Account acquiringAccount;

    @DisplayName("Searching with empty filter returns all saved transactions")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - Transferring Account {0} - Acquiring Account {1}")
    void searchWithoutCriteria(AccountStatus transferringAccountStatus, AccountStatus acquiringAccountStatus)
        throws ParseException {
        initializeData(transferringAccountStatus, acquiringAccountStatus);
        verifyThatSearchReturnsResult(TransactionFilter.builder().build(),
            "Searching with empty criteria should return results");
    }

    @DisplayName("Transaction identifier criterion test")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - Transferring Account {0} - Acquiring Account {1}")
    void searchWithTransactionIdentifier(AccountStatus transferringAccountStatus, AccountStatus acquiringAccountStatus)
        throws ParseException {
        initializeData(transferringAccountStatus, acquiringAccountStatus);

        verifyThatSearchReturnsResult(TransactionFilter
            .builder()
            .transactionId(addTransactionCommand.getIdentifier())
            .build(), "Searching with the transaction identifier that is saved should return results");

        verifyThatSearchReturnsResult(TransactionFilter
                .builder()
                .transactionId(addTransactionCommand.getIdentifier().substring(1, 5))
                .build(),
            "Searching with a part of the saved transaction identifier should return results");

        verifyThatSearchDoesNotReturnResult(TransactionFilter.builder()
                .transactionId(addTransactionCommand.getIdentifier().concat("-NOT-EXISTS"))
                .build(),
            "Searching with a transaction identifier that does not exist in db should not return results");

    }

    @DisplayName("Transaction type criterion test")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - Transferring Account {0} - Acquiring Account {1}")
    void searchWithTransactionType(AccountStatus transferringAccountStatus, AccountStatus acquiringAccountStatus)
        throws ParseException {
        initializeData(transferringAccountStatus, acquiringAccountStatus);
        verifyThatSearchReturnsResult(TransactionFilter.builder()
                .transactionType(addTransactionCommand.getType())
                .build(),
            "searching with the saved transaction type should return result");
        verifyThatSearchDoesNotReturnResult(TransactionFilter.builder()
                .transactionType(Stream.of(TransactionType.values())
                    .filter(type -> type != addTransactionCommand.getType()).findFirst().get())
                .build(),
            "searching with transaction type different than the saved one should return empty result");
    }

    @DisplayName("Transaction status criterion test")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - Transferring Account {0} - Acquiring Account {1}")
    void searchWithTransactionStatus(AccountStatus transferringAccountStatus, AccountStatus acquiringAccountStatus)
        throws ParseException {
        initializeData(transferringAccountStatus, acquiringAccountStatus);
        verifyThatSearchReturnsResult(TransactionFilter.builder()
                .transactionStatus(addTransactionCommand.getStatus())
                .build(),
            "searching with the saved transaction status should return result");
        verifyThatSearchDoesNotReturnResult(TransactionFilter.builder()
                .transactionStatus(Stream.of(TransactionStatus.values())
                    .filter(status -> status != addTransactionCommand.getStatus()).findFirst().get())
                .build(),
            "searching with transaction status different than the saved one should return empty result");
    }

    @DisplayName("Unit type criterion test")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - Transferring Account {0} - Acquiring Account {1}")
    void searchWithUnitType(AccountStatus transferringAccountStatus, AccountStatus acquiringAccountStatus)
        throws ParseException {
        initializeData(transferringAccountStatus, acquiringAccountStatus);
        verifyThatSearchReturnsResult(TransactionFilter.builder()
                .unitType(addTransactionCommand.getUnitType())
                .build(),
            "searching with the saved unit type should return result");
        verifyThatSearchDoesNotReturnResult(TransactionFilter.builder()
                .unitType(Stream.of(UnitType.values())
                    .filter(unitType -> unitType != addTransactionCommand.getUnitType()).findFirst().get())
                .build(),
            "searching with unit type different than the saved one should return empty result");
    }

    @DisplayName("Last update date range criteria test")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - Transferring Account {0} - Acquiring Account {1}")
    void searchWithLastUpdate(AccountStatus transferringAccountStatus, AccountStatus acquiringAccountStatus)
        throws Exception {
        initializeData(transferringAccountStatus, acquiringAccountStatus);
        LocalDate localDate = addTransactionCommand.getLastUpdateDate()
            .toInstant().atZone(ZoneId.systemDefault())
            .toLocalDate();

        Date dateFrom = Date
            .from(localDate.minusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dateUntil = Date
            .from(localDate.plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant());

        verifyThatSearchReturnsResult(TransactionFilter.builder()
                .transactionLastUpdateDateFrom(dateFrom)
                .transactionLastUpdateDateTo(dateUntil)
                .build(),
            "search should return result because last update date is in filter range");

        dateFrom = Date.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        verifyThatSearchDoesNotReturnResult(TransactionFilter.builder()
                .transactionLastUpdateDateFrom(dateFrom)
                .transactionLastUpdateDateTo(dateUntil).build(),
            "search should not return results for last update date out of range");

        verifyThatSearchDoesNotReturnResult(TransactionFilter.builder()
                .transactionLastUpdateDateFrom(dateUntil)
                .transactionLastUpdateDateTo(dateFrom).build(),
            "search should not return results for last update date out of range");
    }

    @DisplayName(("Transferring account number and acquiring account number criteria test"))
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - Transferring Account {0} - Acquiring Account {1}")
    void searchWithAccountNumbers(AccountStatus transferringAccountStatus, AccountStatus acquiringAccountStatus)
        throws ParseException {
        initializeData(transferringAccountStatus, acquiringAccountStatus);
        String AMBITION_INCREASE_CANCELLATION_FULL_IDENTIFIER = "GB-213-908-876";
        verifyThatSearchReturnsResult(TransactionFilter.builder()
                .transferringAccountNumber(AMBITION_INCREASE_CANCELLATION_FULL_IDENTIFIER)
                .acquiringAccountNumber(AMBITION_INCREASE_CANCELLATION_FULL_IDENTIFIER)
                .build(),
            "If the acquiring number is the same as the transferring acquiring number "
                + "then the filter is not an AND but an OR");

        verifyThatSearchReturnsResult(TransactionFilter.builder()
                .transferringAccountNumber(addAcquiringAccountCommand.getFullIdentifier())
                .acquiringAccountNumber(addAcquiringAccountCommand.getFullIdentifier())
                .build(),
            "If the acquiring number is the same as the transferring acquiring number "
                + "then the filter is not an AND but an OR");

        verifyThatSearchReturnsResult(TransactionFilter.builder()
                .transferringAccountNumber(addAcquiringAccountCommand.getFullIdentifier().substring(3, 8))
                .acquiringAccountNumber(addAcquiringAccountCommand.getFullIdentifier().substring(3, 8))
                .build(),
            "If the acquiring number is the same as the transferring acquiring number "
                + "then the filter is not an AND but an OR");

        verifyThatSearchReturnsResult(TransactionFilter.builder()
            .transferringAccountNumber(AMBITION_INCREASE_CANCELLATION_FULL_IDENTIFIER)
            .acquiringAccountNumber(addAcquiringAccountCommand.getFullIdentifier())
            .build(), "The search should return result for searching with the"
            + " saved acquiring account number and the save transferring account number");

        verifyThatSearchReturnsResult(TransactionFilter.builder()
            .transferringAccountNumber(AMBITION_INCREASE_CANCELLATION_FULL_IDENTIFIER.substring(3, 8))
            .acquiringAccountNumber(addAcquiringAccountCommand.getFullIdentifier().substring(3, 8))
            .build(), "The search should return result for searching with the"
            + " saved acquiring account number and the save transferring account number");

        verifyThatSearchReturnsResult(TransactionFilter.builder()
            .transferringAccountNumber(AMBITION_INCREASE_CANCELLATION_FULL_IDENTIFIER.substring(1, 5))
            .acquiringAccountNumber(addAcquiringAccountCommand.getFullIdentifier().substring(1, 6))
            .build(), "The search should return result for searching with the"
            + " saved acquiring account number and the save transferring account number");

        verifyThatSearchDoesNotReturnResult(TransactionFilter.builder()
            .transferringAccountNumber(addAcquiringAccountCommand.getFullIdentifier())
            .acquiringAccountNumber(AMBITION_INCREASE_CANCELLATION_FULL_IDENTIFIER)
            .build(), "search should not return results for non persisted account numbers");
    }

    @DisplayName("Initiator user id criterion test")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - Transferring Account {0} - Acquiring Account {1}")
    void searchWithInitiator(AccountStatus transferringAccountStatus, AccountStatus acquiringAccountStatus)
        throws ParseException {
        initializeData(transferringAccountStatus, acquiringAccountStatus);
        verifyThatSearchReturnsResult(TransactionFilter.builder()
            .initiatorUserId(addInitiatorCommand.getUrid())
            .build(), "Search with urid of saved initiator of saved task of saved transaction should return result");

        verifyThatSearchDoesNotReturnResult(TransactionFilter.builder()
                .initiatorUserId(addInitiatorCommand.getUrid().concat("DOES NOT EXIST"))
                .build(),
            "Search with urid that does not correspond to a saved initiator of saved task of saved transaction should return empty result");
    }

    @DisplayName("Test non admin and authorized representative to transferring account user search")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - Transferring Account {0} - Acquiring Account {1} - " +
        "Expected Results Transferring Account {2} - Expected Results Acquiring Account {3}")
    void searchForNonAdminTransferringAR(AccountStatus transferringAccountStatus, AccountStatus acquiringAccountStatus,
                                         boolean expectedResultsTransferring, boolean expectedResultsAcquiring)
        throws ParseException {
        initializeData(transferringAccountStatus, acquiringAccountStatus);
        String urid = "UK98567654";
        accountModelTestHelper.addUserToAccountAccess(AddUserToAccountAccessCommand
            .builder()
            .account(transferringAccount)
            .enrollmentKey("UK1234567")
            .state(AccountAccessState.ACTIVE)
            .urid(urid)
            .build());

        if (expectedResultsTransferring) {
            verifyThatSearchReturnsResult(TransactionFilter.builder()
                    .enrolledNonAdmin(true)
                    .authorizedRepresentativeUrid(urid)
                    .build(),
                "The saved transaction should be returned to the transferring account Authorised Representative");
        } else {
            verifyThatSearchDoesNotReturnResult(TransactionFilter
                .builder()
                .authorizedRepresentativeUrid(urid)
                .enrolledNonAdmin(true)
                .build(), "No results should be returned for a user who is not admin and the account status " +
                "of the transferring account is SUSPENDED, TRANSFER_PENDING or CLOSED");
        }

        urid = "not-exists-in-account-access-table";
        verifyThatSearchDoesNotReturnResult(TransactionFilter
                .builder()
                .authorizedRepresentativeUrid(urid)
                .enrolledNonAdmin(true)
                .build(),
            "No results should be returned for a user who is not admin and has not any account access to the transferring or the acquiring account");

        urid = "UK9853333333";
        accountModelTestHelper.addUserToAccountAccess(AddUserToAccountAccessCommand
            .builder()
            .account(acquiringAccount)
            .enrollmentKey("UK1234567")
            .state(AccountAccessState.ACTIVE)
            .urid(urid)
            .build());

        if (expectedResultsAcquiring) {
            verifyThatSearchReturnsResult(TransactionFilter.builder()
                    .enrolledNonAdmin(true)
                    .authorizedRepresentativeUrid(urid)
                    .build(),
                "The saved transaction should be returned to the acquiring account Authorised Representative");
        } else {
            verifyThatSearchDoesNotReturnResult(TransactionFilter
                .builder()
                .authorizedRepresentativeUrid(urid)
                .enrolledNonAdmin(true)
                .build(), "No results should be returned for a user who is not admin and the account status " +
                "of the acquiring account is SUSPENDED, TRANSFER_PENDING or CLOSED");
        }

        // TODO: Test all the cases of failure or success for the non admi nenrolled users.
    }


    private void verifyThatSearchReturnsResult(TransactionFilter filter, String message) {
        Page<TransactionProjection> result = repository.search(filter, PageRequest.of(0, 1));
        assertEquals(1, result.getTotalElements(), message);
    }

    private void verifyThatSearchDoesNotReturnResult(TransactionFilter filter, String message) {
        Page<TransactionProjection> result = repository.search(filter, PageRequest.of(0, 1));
        assertEquals(0, result.getTotalElements(), message);
    }

    private void initializeData(AccountStatus transferringAccountStatus, AccountStatus acquiringAccountStatus)
        throws ParseException {

        TransactionModelTestHelper transactionModelTestHelper = new TransactionModelTestHelper(entityManager);
        accountModelTestHelper = new AccountModelTestHelper(entityManager);
        TaskModelTestHelper taskModelTestHelper = new TaskModelTestHelper(entityManager);

        AddAccountHolderCommand addAccountHolderCommand = AddAccountHolderCommand
            .builder()
            .accountHolderType(AccountHolderType.INDIVIDUAL)
            .identifier(1000L)
            .status(Status.ACTIVE)
            .name("TEST 1")
            .build();
        AccountHolder holder = accountModelTestHelper.addAccountHolder(addAccountHolderCommand);

        AddAccountCommand addTransferringAccountCommand = AddAccountCommand.
            builder()
            .accountHolder(holder)
            .accountId(1001L)
            .accountName("transferring account")
            .accountStatus(transferringAccountStatus)
            .complianceStatus(ComplianceStatus.A)
            .fullIdentifier("GB-213-908-876")
            .kyotoAccountType(KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT)
            .registryAccountType(RegistryAccountType.NATIONAL_HOLDING_ACCOUNT)
            .build();
        transferringAccount = accountModelTestHelper.addAccount(addTransferringAccountCommand);

        addAcquiringAccountCommand = AddAccountCommand.
            builder()
            .accountHolder(holder)
            .accountId(1002L)
            .accountName("acquiring account")
            .accountStatus(acquiringAccountStatus)
            .complianceStatus(ComplianceStatus.A)
            .fullIdentifier("GB-213-213-213")
            .kyotoAccountType(KyotoAccountType.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT)
            .registryAccountType(RegistryAccountType.NATIONAL_HOLDING_ACCOUNT)
            .build();
        acquiringAccount = accountModelTestHelper.addAccount(addAcquiringAccountCommand);

        addTransactionCommand = AddTransactionCommand.builder()
            .addTransferringAccountCommand(AddAccountInfoCommand
                .builder()
                .accountFullIdentifier(addTransferringAccountCommand.getFullIdentifier())
                .accountIdentifier(addTransferringAccountCommand.getAccountId())
                .build())
            .addAcquiringAccountCommand(AddAccountInfoCommand
                .builder()
                .accountFullIdentifier(addAcquiringAccountCommand.getFullIdentifier())
                .accountIdentifier(addAcquiringAccountCommand.getAccountId())
                .build())
            .identifier("GB12345")
            .quantity(20L)
            .status(TransactionStatus.COMPLETED)
            .type(TransactionType.IssueOfAAUsAndRMUs)
            .unitType(UnitType.AAU)
            .lastUpdate("12/03/2020 13:23")
            .build();

        BaseTransactionEntity transaction = transactionModelTestHelper.addSearchableTransaction(addTransactionCommand);

        addInitiatorCommand = AddUserCommand.builder()
            .disclosedName("disclosed-name")
            .firstName("first-name")
            .iamIdentifier("initiator-iam-id")
            .lastName("last-name")
            .urid("urid-urid-urid")
            .build();
        User initiator = taskModelTestHelper.addUser(addInitiatorCommand);

        AddUserCommand addApproverCommand = AddUserCommand.builder()
            .disclosedName("approver-disclosed-name")
            .firstName("approver-first-name")
            .iamIdentifier("approver-initiator-iam-id")
            .lastName("approver-last-name")
            .build();
        User approver = taskModelTestHelper.addUser(addApproverCommand);

        AddTaskCommand addTaskCommand = AddTaskCommand.builder()
            .requestId(1234L)
            .initiator(initiator)
            .approver(approver)
            .initiatedDate(new Date())
            .transactionIdentifier(transaction.getIdentifier())
            .build();
        taskModelTestHelper.addTask(addTaskCommand);
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
            Arguments.of(AccountStatus.OPEN, AccountStatus.OPEN, true, true),
            Arguments.of(AccountStatus.TRANSFER_PENDING, AccountStatus.TRANSFER_PENDING, false, false),
            Arguments.of(AccountStatus.CLOSED, AccountStatus.CLOSED, false, false),
            Arguments.of(AccountStatus.SUSPENDED_PARTIALLY, AccountStatus.SUSPENDED_PARTIALLY, false, false),
            Arguments.of(AccountStatus.SUSPENDED, AccountStatus.SUSPENDED, false, false),
            Arguments.of(AccountStatus.TRANSFER_PENDING, AccountStatus.CLOSED, false, false),
            Arguments.of(AccountStatus.CLOSED, AccountStatus.TRANSFER_PENDING, false, false),
            Arguments.of(AccountStatus.TRANSFER_PENDING, AccountStatus.OPEN, false, true),
            Arguments.of(AccountStatus.OPEN, AccountStatus.TRANSFER_PENDING, true, false),
            Arguments.of(AccountStatus.SUSPENDED_PARTIALLY, AccountStatus.OPEN, false, true),
            Arguments.of(AccountStatus.OPEN, AccountStatus.SUSPENDED_PARTIALLY, true, false),
            Arguments.of(AccountStatus.CLOSED, AccountStatus.OPEN, false, true),
            Arguments.of(AccountStatus.OPEN, AccountStatus.CLOSED, true, false),
            Arguments.of(AccountStatus.SUSPENDED, AccountStatus.OPEN, false, true),
            Arguments.of(AccountStatus.OPEN, AccountStatus.SUSPENDED, true, false)
        );
    }
}
