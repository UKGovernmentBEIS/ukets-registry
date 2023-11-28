package gov.uk.ets.registry.api.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionMessage;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionProtocol;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionSystem;
import gov.uk.ets.registry.api.transaction.repository.TransactionMessageRepository;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for transaction messages.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionMessageService {

    private final TransactionMessageRepository transactionMessageRepository;

    private final ObjectMapper objectMapper;

    @Transactional
    public void saveOutgoingMessage(Transaction transaction, String content, TransactionSystem to, Serializable payload) {
        save(transaction, content, TransactionSystem.REGISTRY, to, payload);
    }

    @Transactional
    public void saveInboundMessage(Transaction transaction, String content, TransactionSystem in, Serializable payload) {
        save(transaction, content, in, TransactionSystem.REGISTRY, payload);
    }

    @SneakyThrows(JsonProcessingException.class)
    @Transactional
    public void save(Transaction transaction, String content, TransactionSystem from, TransactionSystem to,
                     Serializable payload) {
        TransactionMessage message = new TransactionMessage();
        message.setMessage(content);
        message.setTransaction(transaction);
        message.setDate(new Date());
        message.setSender(from);
        message.setRecipient(to);
        message.setPayload(objectMapper.writeValueAsString(payload));
        Optional<TransactionProtocol> protocol = Stream.of(from, to).map(TransactionSystem::getProtocol)
            .filter(Objects::nonNull).findFirst();
        protocol.ifPresent(message::setProtocol);
        transactionMessageRepository.save(message);
    }

}
