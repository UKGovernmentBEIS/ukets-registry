package uk.gov.ets.itl.webservices.client.messaging.listener;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogPort;
import uk.gov.ets.kp.webservices.shared.types.MessageRequest;
import uk.gov.ets.kp.webservices.shared.types.MessageResponse;
import uk.gov.ets.kp.webservices.shared.types.NotificationRequest;
import uk.gov.ets.kp.webservices.shared.types.NotificationResponse;
import uk.gov.ets.kp.webservices.shared.types.ProposalRequest;
import uk.gov.ets.kp.webservices.shared.types.ProposalResponse;
import uk.gov.ets.kp.webservices.shared.types.TransactionStatusRequest;
import uk.gov.ets.kp.webservices.shared.types.TransactionStatusResponse;

/**
 * This class listens for messages published on the "proposal.notification.out" topic of Kafka.
 * It is responsible for forwarding these messages to the ITL.
 * 
 * @author P35036
 * @since v.0.5.0
 */
@Service
@KafkaListener(topics = "proposal.notification.out")
public class RegistryMessageListener {

    private static final Logger log = LoggerFactory.getLogger(RegistryMessageListener.class);
	private TransactionLogPort transactionLogPort;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
	public RegistryMessageListener(TransactionLogPort transactionLogPort) {
		this.transactionLogPort = transactionLogPort;
	}

	@KafkaHandler
	public void handleMessageRequest(uk.gov.ets.lib.commons.kyoto.types.MessageRequest request) {
		try {
	          log.info("Sending MessageRequest to ITL");
		    MessageRequest messageRequest = objectMapper
                .readValue(objectMapper.writeValueAsString(request), MessageRequest.class);
			MessageResponse response = transactionLogPort.acceptMessage(messageRequest);
		} catch (RemoteException | JsonProcessingException e) {
            log.error("MessageRequest call failed", e);
		}
	}

	@KafkaHandler
	public void handleTransactionStatusRequest(uk.gov.ets.lib.commons.kyoto.types.TransactionStatusRequest request) {
		try {
	        log.info("Sending TransactionStatusRequest to ITL for {}" + request.getTransactionIdentifier());
		    TransactionStatusRequest transactionStatusRequest = objectMapper
                .readValue(objectMapper.writeValueAsString(request), TransactionStatusRequest.class);
			TransactionStatusResponse response = transactionLogPort.getTransactionStatus(transactionStatusRequest);
		} catch (RemoteException | JsonProcessingException e) {
            log.error("TransactionStatusRequest call failed", e);
		}
	}
	
	@KafkaHandler
	public void handleAcceptNotificationRequest(uk.gov.ets.lib.commons.kyoto.types.NotificationRequest request) {
		try {
            log.info("Sending NotificationRequest to ITL for {}",request.getTransactionIdentifier());
		    NotificationRequest notificationRequest = objectMapper
                .readValue(objectMapper.writeValueAsString(request), NotificationRequest.class);	    
			NotificationResponse response = transactionLogPort.acceptNotification(notificationRequest);
		} catch (RemoteException | JsonProcessingException e) {
            log.error("NotificationRequest call failed", e);
		}
	}	
	
	@KafkaHandler
	public void handleAcceptProposalRequest(uk.gov.ets.lib.commons.kyoto.types.ProposalRequest request) {
		try {
	        log.info("Sending ProposalRequest {} to ITL" , request.getProposedTransaction().getTransactionIdentifier());
		    ProposalRequest proposalRequest = objectMapper
                .readValue(objectMapper.writeValueAsString(request), ProposalRequest.class);     		    
			ProposalResponse response = transactionLogPort.acceptProposal(proposalRequest);
		} catch (RemoteException | JsonProcessingException e) {
            log.error("ProposalRequest call failed", e);
		}
	}	
}
