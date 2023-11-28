package gov.uk.ets.registry.api.transaction.messaging;

import gov.uk.ets.registry.api.itl.message.ITLMessageService;
import gov.uk.ets.registry.api.transaction.TransactionService;
import lombok.extern.log4j.Log4j2;
import uk.gov.ets.lib.commons.kyoto.types.MessageRequest;
import uk.gov.ets.lib.commons.kyoto.types.NotificationRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProposalRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProvideTimeRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service for receiving messages from ITL.
 *
 * @author P35036
 * @since v0.5.0
 */
@Log4j2
@Service
@KafkaListener(topics = "proposal.notification.in")
public class ITLIncomingMessageListener {

    /**
     * Service for transactions.
     */
    @Autowired
    private TransactionService transactionService;
    /**
     * Service for ITL instant messaging.
     */
    @Autowired
    private ITLMessageService messageService;
    
    /**
     * Handles the incoming message request.
     * @param request The message request.
     */
    @KafkaHandler
    public void handleMessageRequest(MessageRequest request) {
        log.info("Received a message request from ITL: {}", request);
        messageService.processIncomingMessage(request);
    }

    /**
     * Handles the incoming provided time request.
     * @param request The time request.
     */
    @KafkaHandler
    public void handleProvideTimeRequest(ProvideTimeRequest request) {
        log.info("Received a provide time request from ITL: {}", request);
    }

    /**
     * Handles the incoming proposal request.
     * @param request The proposal request.
     */
    @KafkaHandler
    public void handleProposalRequest(ProposalRequest request) {
        transactionService.processIncomingTransaction(request);
        log.info("Received a proposal request from ITL: {}", request);
    }

    /**
     * Handles the incoming notification request.
     * @param request The notification request.
     */
    @KafkaHandler
    public void handleNotificationRequest(NotificationRequest request) {
        transactionService.processReply(request);
        log.info("Received a notification request from ITL: {} with status {}", request, request.getTransactionStatus());
    }
}
