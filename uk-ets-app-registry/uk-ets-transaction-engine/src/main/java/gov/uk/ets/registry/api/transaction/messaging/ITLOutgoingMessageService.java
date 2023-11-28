package gov.uk.ets.registry.api.transaction.messaging;

import java.io.Serializable;
import lombok.extern.log4j.Log4j2;
import uk.gov.ets.lib.commons.kyoto.types.MessageRequest;
import uk.gov.ets.lib.commons.kyoto.types.NotificationRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProposalRequest;
import uk.gov.ets.lib.commons.kyoto.types.TransactionStatusRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for sending messages to ITL.
 */
@Log4j2
@Service
public class ITLOutgoingMessageService {

    @Value("${spring.kafka.template.default-topic}")
    private String proposalNotificationOut;

    @Qualifier("itlTransactionProducerTemplate")
    private final KafkaTemplate<String, Serializable> transactionProducerTemplate;

    /**
     * Constructor.
     */
    public ITLOutgoingMessageService(KafkaTemplate<String, Serializable> transactionProducerTemplate) {
        this.transactionProducerTemplate = transactionProducerTemplate;
    }

    /**
     * Sends a message request.
     *
     * @param request The message request.
     */
    public void sendMessageRequest(MessageRequest request) {
        log.info("Sending a message request {}", request);
        try {
            transactionProducerTemplate.send(proposalNotificationOut, request).get();
        } catch (Exception e) {
            log.error("MessageRequest message failed to be send: {}", request);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a transaction status request.
     *
     * @param request The transaction status request.
     */
    public void sendTransactionStatusRequest(TransactionStatusRequest request) {
        log.info("Sending a transaction status request {}", request);
        transactionProducerTemplate.send(proposalNotificationOut, request);
    }

    /**
     * Sends a notification request.
     *
     * @param request The notification request.
     */
    public void sendNotificationRequest(NotificationRequest request) {
        log.info("Sending a notification request {}", request);
        transactionProducerTemplate.send(proposalNotificationOut, request);
    }

    /**
     * Sends a proposal request.
     *
     * @param request The proposal request.
     */
    public void sendProposalRequest(ProposalRequest request) {
        log.info("Sending a proposal request {}", request);
        transactionProducerTemplate.send(proposalNotificationOut, request);
    }
}
