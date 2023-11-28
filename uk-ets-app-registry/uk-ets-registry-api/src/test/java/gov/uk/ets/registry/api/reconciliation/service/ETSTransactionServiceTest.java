package gov.uk.ets.registry.api.reconciliation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper.AddAccountInfoCommand;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper.AddTransactionCommand;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import lombok.Builder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create"})
class ETSTransactionServiceTest {

    static List<TransactionStatus> nonPendingStatuses;

    static List<TransactionType> ukTransactionTypes;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    TransactionRepository transactionRepository;

    ETSTransactionService etsTransactionService;

    TransactionModelTestHelper transactionModelTestHelper;

    @BeforeAll
    static void init() {
        nonPendingStatuses = List.of(
            TransactionStatus.AWAITING_APPROVAL,
            TransactionStatus.DELAYED,
            TransactionStatus.COMPLETED,
            TransactionStatus.TERMINATED,
            TransactionStatus.REJECTED,
            TransactionStatus.CANCELLED,
            TransactionStatus.REVERSED,
            TransactionStatus.MANUALLY_CANCELLED,
            TransactionStatus.FAILED);

        ukTransactionTypes = Stream.of(TransactionType.values()).filter(transactionType -> !transactionType.isKyoto())
            .collect(
                Collectors.toList());
    }

    @BeforeEach
    void setUp() {
        transactionModelTestHelper = new TransactionModelTestHelper(entityManager);
        etsTransactionService = new ETSTransactionService(transactionRepository);
    }

    @DisplayName("countPendingETSTransactions should return 1 if a pending ets transaction exists")
    @Test
    void givenPendingUKTransactionsExistThenItShouldReturnOne() throws ParseException {
        TransactionType transactionType = ukTransactionTypes.get(0);
        TransactionStatus transactionStatus = Stream
            .of(TransactionStatus.values()).filter(status ->
            !nonPendingStatuses.contains(status)).findAny().get();
        // given
        persistTestTransaction(transactionType, transactionStatus);

        // when then
        assertTrue(etsTransactionService.countPendingETSTransactions() > 0);
    }

    @DisplayName("countPendingETSTransactions should return 0 when pending transaction does not exist")
    @Test
    void givenPendingUKTransactionDoesNotExistThenItShouldReturnZeroIf()
        throws ParseException {
        TransactionType transactionType = ukTransactionTypes.get(0);
        TransactionStatus transactionStatus = nonPendingStatuses.get(0);
        // given
        persistTestTransaction(transactionType, transactionStatus);

        // when then
        assertTrue(etsTransactionService.countPendingETSTransactions() == 0);
    }

    @DisplayName("countPendingETSTransactions should return 0 when a pending non ETS exists but a pending ets transaction does not exist")
    @Test
    void givenPendingButNonUKTransactionExistsThenItShouldReturnZero()
        throws ParseException {
        TransactionType transactionType = Stream.of(TransactionType.values()).filter(t -> t.isKyoto()).findAny().get();
        TransactionStatus transactionStatus = Stream
            .of(TransactionStatus.values()).filter(status ->
                !nonPendingStatuses.contains(status)).findAny().get();
        // given
        persistTestTransaction(transactionType, transactionStatus);
        // when then
        assertTrue(etsTransactionService.countPendingETSTransactions() == 0);
    }

    private void persistTestTransaction(TransactionType transactionType, TransactionStatus transactionStatus)
        throws ParseException {
        transactionModelTestHelper.addSearchableTransaction(AddTransactionCommand
            .builder()
            .addAcquiringAccountCommand(AddAccountInfoCommand.builder()
                .accountFullIdentifier("acquiring-test-account-key")
                .accountRegistryCode("UK")
                .build())
            .addTransferringAccountCommand(AddAccountInfoCommand.builder()
                .accountFullIdentifier("transferring-test-account-key")
                .accountRegistryCode("UK")
                .build())
            .identifier("GB12345")
            .quantity(20L)
            .status(transactionStatus)
            .type(transactionType)
            .unitType(UnitType.AAU)
            .lastUpdate("12/03/2020 13:23")
            .build());
    }

    @Builder
    static class GetETSTransactionsStartedAfterTestCase {
        private Date afterDate;
        private Set<Long> expectedResultsIdentifiers;
        private List<PersistTestTransactionCommand> commands;
    }

    @Builder
    static class PersistTestTransactionCommand {
        private String identifier;
        private boolean isKyoto;
        private Date startedDate;
        private Long acquiringAccountIdentifier;
        private Long transferringAccountIdentifier;
    }
}