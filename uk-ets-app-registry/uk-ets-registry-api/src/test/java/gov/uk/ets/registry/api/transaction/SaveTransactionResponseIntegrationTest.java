package gov.uk.ets.registry.api.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper.AddAccountInfoCommand;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper.AddTransactionCommand;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionResponse;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.AccountTotalRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionBlockRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionConnectionRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionHistoryRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionResponseRepository;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import gov.uk.ets.registry.api.transaction.service.TransactionResponseDTO;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
class SaveTransactionResponseIntegrationTest {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionBlockRepository transactionBlockRepository;
    @Autowired
    private UnitBlockRepository unitBlockRepository;
    @Autowired
    private AccountTotalRepository accountTotalRepository;
    @Autowired
    private TransactionResponseRepository transactionResponseRepository;
    @Autowired
    private TransactionHistoryRepository historyRepository;
    @Autowired
    private TransactionConnectionRepository transactionConnectionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private TransactionModelTestHelper transactionModelTestHelper;

    private TransactionPersistenceService transactionPersistenceService;

    @BeforeEach
    void setup() {
        transactionPersistenceService = new TransactionPersistenceService(transactionRepository,
            transactionBlockRepository, unitBlockRepository, accountTotalRepository, transactionResponseRepository,
            historyRepository, transactionConnectionRepository);
        transactionPersistenceService.setEntityManager(entityManager);
        transactionModelTestHelper = new TransactionModelTestHelper(entityManager);
    }

    @Transactional
    @Test
    void saveTransactionResponse() throws Exception{
        // given
        AddTransactionCommand addTransactionCommand = AddTransactionCommand
            .builder()
            .addAcquiringAccountCommand(AddAccountInfoCommand.builder()
                .accountFullIdentifier("acquiring-test-account-key")
                .accountRegistryCode("UK")
                .accountIdentifier(1000L)
                .accountType(KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT)
                .build())
            .addTransferringAccountCommand(AddAccountInfoCommand.builder()
                .accountFullIdentifier("transferring-test-account-key")
                .accountIdentifier(2000L)
                .accountRegistryCode("UK")
                .accountType(KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT)
                .build())
            .identifier("GB12345")
            .quantity(20L)
            .status(TransactionStatus.REJECTED)
            .type(TransactionType.CentralTransferAllowances)
            .unitType(UnitType.AAU)
            .lastUpdate("12/03/2020 13:23")
            .build();
        Transaction transaction =  transactionModelTestHelper.addTransaction(addTransactionCommand);
        BusinessCheckError expectedError = new BusinessCheckError(10, "Some error");
        TransactionResponseDTO dto = TransactionResponseDTO.builder()
            .transaction(transaction)
            .errors(List.of(expectedError))
            .build();

        // when
        transactionPersistenceService.saveTransactionResponse(dto);

        //then
        List<TransactionResponse> transactionResponses = transactionResponseRepository.findAll();
        assertEquals(1, transactionResponses.size());
        TransactionResponse response = transactionResponses.get(0);
        assertNotNull(response.getDateOccurred());
        assertEquals(expectedError.getMessage(), response.getDetails());
        assertEquals(expectedError.getCode(), response.getErrorCode().intValue());
    }





}
