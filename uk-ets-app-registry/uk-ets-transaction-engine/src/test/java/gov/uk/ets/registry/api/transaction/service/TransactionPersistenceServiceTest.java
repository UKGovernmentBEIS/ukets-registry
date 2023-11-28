package gov.uk.ets.registry.api.transaction.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionResponse;
import gov.uk.ets.registry.api.transaction.repository.AccountTotalRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionBlockRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionConnectionRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionHistoryRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionResponseRepository;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class TransactionPersistenceServiceTest {

    private TransactionPersistenceService transactionPersistenceService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionResponseRepository transactionResponseRepository;

    @BeforeEach
    void setUp() {
        transactionPersistenceService = new TransactionPersistenceService(
            transactionRepository,
            mock(TransactionBlockRepository.class),
            mock(UnitBlockRepository.class),
            mock(
                AccountTotalRepository.class),
            transactionResponseRepository,
            mock(TransactionHistoryRepository.class),
            mock(TransactionConnectionRepository.class));
    }

    @Test
    @Transactional
    void saveTransactionResponse() {
        // given
        String transactionIdentifier = "UK1234567";
        Long code = 12345L;
        String message = "dummy error";
        List<BusinessCheckError> errors = List.of(new BusinessCheckError(code.intValue(), message));
        Transaction transaction = new Transaction();
        transaction.setIdentifier(transactionIdentifier);

        TransactionResponseDTO dto = TransactionResponseDTO.builder()
            .transaction(transaction)
            .errors(errors)
            .build();

        // when
        transactionPersistenceService.saveTransactionResponse(dto);

        // then
        then(transactionRepository).should(times(0)).findByIdentifier(transactionIdentifier);
        ArgumentCaptor<TransactionResponse> captor = ArgumentCaptor.forClass(TransactionResponse.class);
        then(transactionResponseRepository).should(times(1)).save(captor.capture());
        TransactionResponse response = captor.getValue();
        assertEquals(message, response.getDetails());
        assertNotNull(response.getDateOccurred());
        assertEquals(code, response.getErrorCode());
        assertNull(response.getTransactionBlockId());
        assertNotNull(response.getTransaction());
        assertEquals(transactionIdentifier, response.getTransaction().getIdentifier());
    }




}
