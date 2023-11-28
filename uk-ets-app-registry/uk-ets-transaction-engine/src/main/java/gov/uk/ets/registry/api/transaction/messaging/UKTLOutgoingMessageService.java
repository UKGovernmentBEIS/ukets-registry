package gov.uk.ets.registry.api.transaction.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class UKTLOutgoingMessageService {

    @Qualifier("uktlTransactionProducerTemplate")
    private final KafkaTemplate<String, TransactionNotification> uktlTransactionProducerTemplate;


    @Value("${kafka.transaction-uktl.question.topic}")
    private String transactionQuestionTopic;

    /**
     * Sends a transaction finalization request to the UK ETS Transaction Log.
     *
     * @param message The transaction data transfer object
     */
    public void sendProposalRequest(TransactionNotification message) {
        log.info("Sending transaction:{} to UKTL ", message.getIdentifier());
        try {
            uktlTransactionProducerTemplate.send(transactionQuestionTopic, message).get();
        } catch (Exception e) {
            log.error("TransactionNotification message failed to be send: {}", message);
            throw new RuntimeException(e);
        }
    }
}
