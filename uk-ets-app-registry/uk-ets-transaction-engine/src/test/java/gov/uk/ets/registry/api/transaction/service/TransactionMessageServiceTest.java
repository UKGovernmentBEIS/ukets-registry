package gov.uk.ets.registry.api.transaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionMessage;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionProtocol;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionSystem;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.messaging.TransactionNotification;
import gov.uk.ets.registry.api.transaction.messaging.UKTLTransactionAnswer;
import gov.uk.ets.registry.api.transaction.repository.TransactionMessageRepository;
import uk.gov.ets.lib.commons.kyoto.types.NotificationRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionMessageServiceTest {

    @Mock
    TransactionMessageRepository transactionMessageRepository;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    TransactionMessageService transactionMessageService;

    @Captor
    ArgumentCaptor<TransactionMessage> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMessage() {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.ExternalTransfer);

        transactionMessageService.saveOutgoingMessage(
            transaction, TransactionStatus.COMPLETED.name(), TransactionSystem.ITL, new NotificationRequest());

        verify(transactionMessageRepository).save(captor.capture());
        TransactionMessage message = captor.getValue();
        assertEquals(TransactionProtocol.KYOTO, message.getProtocol());
        assertEquals(TransactionSystem.REGISTRY, message.getSender());

        transaction.setType(TransactionType.SurrenderAllowances);
        transactionMessageService.saveOutgoingMessage(
            transaction, TransactionStatus.PROPOSED.name(), TransactionSystem.UKTL,
            TransactionNotification.from(transaction));

        verify(transactionMessageRepository, times(2)).save(captor.capture());
        message = captor.getValue();
        assertEquals(TransactionProtocol.UKETS, message.getProtocol());
        assertEquals(TransactionSystem.REGISTRY, message.getSender());

        transactionMessageService.saveInboundMessage(
            new Transaction(), TransactionStatus.CHECKED_DISCREPANCY.name(), TransactionSystem.ITL,
            new NotificationRequest());

        verify(transactionMessageRepository, times(3)).save(captor.capture());
        message = captor.getValue();
        assertEquals(TransactionProtocol.KYOTO, message.getProtocol());
        assertEquals(TransactionSystem.REGISTRY, message.getRecipient());

        transactionMessageService.saveInboundMessage(
            new Transaction(), TransactionStatus.STL_CHECKED_DISCREPANCY.name(), TransactionSystem.UKTL,
            new UKTLTransactionAnswer());

        verify(transactionMessageRepository, times(4)).save(captor.capture());
        message = captor.getValue();
        assertEquals(TransactionProtocol.UKETS, message.getProtocol());
        assertEquals(TransactionSystem.REGISTRY, message.getRecipient());
    }

}
