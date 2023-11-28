package uk.gov.ets.kp.webservices.registry;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.ets.kp.webservices.shared.enums.ResultIdentifier;
import uk.gov.ets.kp.webservices.shared.types.ITLNoticeRequest;
import uk.gov.ets.kp.webservices.shared.types.ITLNoticeResponse;
import uk.gov.ets.kp.webservices.shared.types.InitiateReconciliationRequest;
import uk.gov.ets.kp.webservices.shared.types.InitiateReconciliationResponse;
import uk.gov.ets.kp.webservices.shared.types.MessageRequest;
import uk.gov.ets.kp.webservices.shared.types.MessageResponse;
import uk.gov.ets.kp.webservices.shared.types.NotificationRequest;
import uk.gov.ets.kp.webservices.shared.types.NotificationResponse;
import uk.gov.ets.kp.webservices.shared.types.ProposalRequest;
import uk.gov.ets.kp.webservices.shared.types.ProposalResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideTimeRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideTimeResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideTotalsRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse;
import uk.gov.ets.kp.webservices.shared.types.ReconciliationResultRequest;
import uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse;

/**
 * Spring Bean that consumes the API requests and publishes them to the kafka topic.
 */
public class RegistryService {
    private KafkaTemplate kafkaTemplate;

    private KafkaTopics kafkaTopics;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Log log = LogFactory.getLog(RegistryService.class);

    public RegistryService(KafkaTemplate kafkaTemplate, KafkaTopics kafkaTopics) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopics = kafkaTopics;
    }

    public ProvideTimeResponse provideTime(ProvideTimeRequest provideTimeRequest) {
        log.info("Received ProvideTimeRequest");
        ProvideTimeResponse response = new ProvideTimeResponse();
        response.setSystemTime(Calendar.getInstance());
        try {
            uk.gov.ets.lib.commons.kyoto.types.ProvideTimeRequest request = objectMapper
                .readValue(objectMapper.writeValueAsString(provideTimeRequest), uk.gov.ets.lib.commons.kyoto.types.ProvideTimeRequest.class);
            ((KafkaTemplate<String, uk.gov.ets.lib.commons.kyoto.types.ProvideTimeRequest>)kafkaTemplate).sendDefault(request);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode());
        } catch (JsonProcessingException e) {
            log.error("Error processing ProvideTimeRequest." , e);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_REJECTED.getCode());
        }
        return response;
    }

    public MessageResponse acceptMessage(MessageRequest acceptMessageRequest)  {
        log.info("Received MessageRequest");
        MessageResponse response = new MessageResponse();
        uk.gov.ets.lib.commons.kyoto.types.MessageRequest request;
        try {
            request = objectMapper
                .readValue(objectMapper.writeValueAsString(acceptMessageRequest), uk.gov.ets.lib.commons.kyoto.types.MessageRequest.class);
            ((KafkaTemplate<String, uk.gov.ets.lib.commons.kyoto.types.MessageRequest>)kafkaTemplate).sendDefault(request);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode());
        } catch (JsonProcessingException e) {
            log.error("Error processing MessageRequest." , e);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_REJECTED.getCode());
        }
        return response;
    }

    public ProposalResponse acceptProposal(ProposalRequest acceptProposalRequest)  {
        log.info("Received ProposalRequest for " + acceptProposalRequest.getProposedTransaction().getTransactionIdentifier());
        ProposalResponse response = new ProposalResponse();
        try {
            uk.gov.ets.lib.commons.kyoto.types.ProposalRequest request = objectMapper
                .readValue(objectMapper.writeValueAsString(acceptProposalRequest), uk.gov.ets.lib.commons.kyoto.types.ProposalRequest.class);
            ((KafkaTemplate<String, uk.gov.ets.lib.commons.kyoto.types.ProposalRequest>) kafkaTemplate).sendDefault(request);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode());
        } catch (JsonProcessingException e) {
            log.error("Error processing ProposalRequest." , e);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_REJECTED.getCode());
        }
        return response;
    }

    public NotificationResponse acceptNotification(NotificationRequest acceptNotificationRequest) {
        log.info("Received NotificationRequest for " + acceptNotificationRequest.getTransactionIdentifier());
        NotificationResponse response = new NotificationResponse();
        try {
            uk.gov.ets.lib.commons.kyoto.types.NotificationRequest request = objectMapper
                .readValue(objectMapper.writeValueAsString(acceptNotificationRequest), uk.gov.ets.lib.commons.kyoto.types.NotificationRequest.class);
            ((KafkaTemplate<String, uk.gov.ets.lib.commons.kyoto.types.NotificationRequest>) kafkaTemplate).sendDefault(request);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode());
        } catch (JsonProcessingException e) {
            log.error("Error processing NotificationRequest." , e);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_REJECTED.getCode());
        }
        return response;
    }

    public ITLNoticeResponse acceptITLNotice(ITLNoticeRequest acceptITLNoticeRequest) {
        log.info("Received ITLNoticeRequest " + acceptITLNoticeRequest.getNotificationIdentifier());
        ITLNoticeResponse response = new ITLNoticeResponse();
        try {
            uk.gov.ets.lib.commons.kyoto.types.ITLNoticeRequest request = objectMapper
                .readValue(objectMapper.writeValueAsString(acceptITLNoticeRequest), uk.gov.ets.lib.commons.kyoto.types.ITLNoticeRequest.class);      
            ((KafkaTemplate<String, uk.gov.ets.lib.commons.kyoto.types.ITLNoticeRequest>) kafkaTemplate).send(kafkaTopics.getNoticesKafkaTopic(), request);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode());
        } catch (JsonProcessingException e) {
            log.error("Error processing ITLNoticeRequest." , e);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_REJECTED.getCode());
        }
        return response;
    }

    public InitiateReconciliationResponse initiateReconciliation(InitiateReconciliationRequest initiateReconciliationRequest) {
        log.info("Received InitiateReconciliationRequest " + initiateReconciliationRequest.getReconciliationIdentifier());
        InitiateReconciliationResponse response = new InitiateReconciliationResponse();
        try {
            uk.gov.ets.lib.commons.kyoto.types.InitiateReconciliationRequest request = objectMapper
                .readValue(objectMapper.writeValueAsString(initiateReconciliationRequest), uk.gov.ets.lib.commons.kyoto.types.InitiateReconciliationRequest.class); 
            ((KafkaTemplate<String, uk.gov.ets.lib.commons.kyoto.types.InitiateReconciliationRequest>) kafkaTemplate)
                .send(kafkaTopics.getReconciliationInTopic(), request);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode());
        } catch (JsonProcessingException e) {
            log.error("Error processing InitiateReconciliationRequest." , e);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_REJECTED.getCode());
        }
        return response;
    }

    public ReconciliationResultResponse receiveReconciliationResult(ReconciliationResultRequest receiveReconciliationResultRequest) {
        log.info("Received ReconciliationResultRequest for " + receiveReconciliationResultRequest.getReconciliationIdentifier());
        ReconciliationResultResponse response = new ReconciliationResultResponse();
        try {
            uk.gov.ets.lib.commons.kyoto.types.ReconciliationResultRequest request = objectMapper
                .readValue(objectMapper.writeValueAsString(receiveReconciliationResultRequest), uk.gov.ets.lib.commons.kyoto.types.ReconciliationResultRequest.class);
            ((KafkaTemplate<String, uk.gov.ets.lib.commons.kyoto.types.ReconciliationResultRequest>) kafkaTemplate)
                .send(kafkaTopics.getReconciliationInTopic(), request);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode());
        } catch (JsonProcessingException e) {
            log.error("Error processing ReconciliationResultRequest." , e);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_REJECTED.getCode());
        }
        return response;
    }

    public ProvideAuditTrailResponse provideAuditTrail(ProvideAuditTrailRequest provideAuditTrailRequest) {
        log.info("Received ProvideAuditTrailRequest for " + provideAuditTrailRequest.getReconciliationIdentifier());
        ProvideAuditTrailResponse response = new ProvideAuditTrailResponse();
        try {
            uk.gov.ets.lib.commons.kyoto.types.ProvideAuditTrailRequest request = objectMapper
                .readValue(objectMapper.writeValueAsString(provideAuditTrailRequest), uk.gov.ets.lib.commons.kyoto.types.ProvideAuditTrailRequest.class);
            ((KafkaTemplate<String, uk.gov.ets.lib.commons.kyoto.types.ProvideAuditTrailRequest>) kafkaTemplate)
                .send(kafkaTopics.getReconciliationInTopic(), request);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode());
        } catch (JsonProcessingException e) {
            log.error("Error processing ProvideAuditTrailRequest." , e);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_REJECTED.getCode());
        }
        return response;
    }

    public ProvideTotalsResponse provideTotals(ProvideTotalsRequest provideTotalsRequest) {
        log.info("Received ProvideTotalsRequest for " + provideTotalsRequest.getReconciliationIdentifier());
        ProvideTotalsResponse response = new ProvideTotalsResponse();
        try {
            uk.gov.ets.lib.commons.kyoto.types.ProvideTotalsRequest request = objectMapper
                .readValue(objectMapper.writeValueAsString(provideTotalsRequest), uk.gov.ets.lib.commons.kyoto.types.ProvideTotalsRequest.class);
            ((KafkaTemplate<String, uk.gov.ets.lib.commons.kyoto.types.ProvideTotalsRequest>) kafkaTemplate)
                .send(kafkaTopics.getReconciliationInTopic(), request);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode());
        } catch (JsonProcessingException e) {
            log.error("Error processing ProvideTotalsRequest." , e);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_REJECTED.getCode());
        }      
        return response;
    }

    public ProvideUnitBlocksResponse provideUnitBlocks(ProvideUnitBlocksRequest provideUnitBlocksRequest) {
        log.info("Received ProvideUnitBlocksRequest for " + provideUnitBlocksRequest.getReconciliationIdentifier());
        ProvideUnitBlocksResponse response = new ProvideUnitBlocksResponse();
        try {
            uk.gov.ets.lib.commons.kyoto.types.ProvideUnitBlocksRequest request = objectMapper
                .readValue(objectMapper.writeValueAsString(provideUnitBlocksRequest), uk.gov.ets.lib.commons.kyoto.types.ProvideUnitBlocksRequest.class);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode());
            ((KafkaTemplate<String, uk.gov.ets.lib.commons.kyoto.types.ProvideUnitBlocksRequest>) kafkaTemplate)
                .send(kafkaTopics.getReconciliationInTopic(), request);
        } catch (JsonProcessingException e) {
            log.error("Error processing ProvideUnitBlocksRequest." , e);
            response.setResultIdentifier(ResultIdentifier.SOAP_MESSAGE_REJECTED.getCode());
        }
        return response;
    }

}
