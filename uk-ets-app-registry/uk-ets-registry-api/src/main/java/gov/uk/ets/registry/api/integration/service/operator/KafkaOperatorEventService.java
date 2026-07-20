package gov.uk.ets.registry.api.integration.service.operator;

import static uk.ets.lib.commons.kafkaconfig.KafkaConstants.CORRELATION_ID_HEADER;
import static uk.ets.lib.commons.kafkaconfig.KafkaConstants.CORRELATION_PARENT_ID_HEADER;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.integration.notification.IntegrationPointDisabledNotificationProducer;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("${kafka.integration.enabled:false}")
public class KafkaOperatorEventService implements OperatorEventService {

    private static final List<String> SUPPORTED_ACCOUNT_TYPES = List.of(
            AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.name(),
            AccountType.OPERATOR_HOLDING_ACCOUNT.name(),
            AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.name());

    @Value("${kafka.integration.installation.set.operator.request.topic}")
    private String installationSetOperatorRequestTopic;
    @Value("${kafka.integration.aviation.set.operator.request.topic}")
    private String aviationSetOperatorRequestTopic;
    @Value("${kafka.integration.maritime.set.operator.request.topic}")
    private String maritimeSetOperatorRequestTopic;   
    @Value("${kafka.integration.installation.set.operator.enabled}")
    private boolean installationSetOperatorEnabled;
    @Value("${kafka.integration.aviation.set.operator.enabled}")
    private boolean aviationSetOperatorEnabled;
    @Value("${kafka.integration.maritime.set.operator.enabled}")
    private boolean maritimeSetOperatorEnabled;
    
    private final IntegrationPointDisabledNotificationProducer integrationPointDisabledNotificationProducer;
    private final KafkaTemplate<String, OperatorUpdateEvent> kafkaTemplate;

    @Override
    public void updateOperator(CompliantEntity operator, String accountType) {
        updateOperator(operator, accountType,Optional.empty());
    }    
    
    @Override
    public void updateOperator(CompliantEntity operator, String accountType, Optional<String> parentCorrelationIdOptional) {

        if (!SUPPORTED_ACCOUNT_TYPES.contains(accountType)) {
            return;
        }

        OperatorUpdateEvent operatorUpdateEvent = new OperatorUpdateEvent();
        operatorUpdateEvent.setOperatorId(operator.getIdentifier());
        operatorUpdateEvent.setEmitterId(operator.getEmitterId());
        operatorUpdateEvent.setRegulator(operator.getRegulator().name());

        String correlationId = UUID.randomUUID().toString();
        
        if (AccountType.OPERATOR_HOLDING_ACCOUNT.name().equals(accountType) && !installationSetOperatorEnabled) {
            log.debug("Installation set Operator IP disabled.");
            integrationPointDisabledNotificationProducer.sendNotifications(operatorUpdateEvent,
                    OperationEvent.SET_OPERATOR_ID,correlationId,SourceSystem.METSIA_INSTALLATION);
            return;
        }
        if (AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.name().equals(accountType) && !aviationSetOperatorEnabled) {
            log.debug("Aircraft set Operator IP disabled.");
            integrationPointDisabledNotificationProducer.sendNotifications(operatorUpdateEvent,
                    OperationEvent.SET_OPERATOR_ID,correlationId,SourceSystem.METSIA_AVIATION);
            return;
        }
        if (AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.name().equals(accountType) && !maritimeSetOperatorEnabled) {
            log.debug("Maritime set Operator IP disabled.");
            integrationPointDisabledNotificationProducer.sendNotifications(operatorUpdateEvent,
                    OperationEvent.SET_OPERATOR_ID,correlationId,SourceSystem.MARITIME);
            return;
        }        
        
        RecordHeaders headers = new RecordHeaders();
        //Set parent correlation id
        if (parentCorrelationIdOptional.isPresent()) {
            headers.add(CORRELATION_PARENT_ID_HEADER, parentCorrelationIdOptional.get().getBytes());
            log.info("Setting header CORRELATION_PARENT_ID_HEADER for update operatorId, parentCorrelationId : {} for correlationId: {}", parentCorrelationIdOptional.get(),correlationId);
        }
        headers.add(CORRELATION_ID_HEADER, correlationId.getBytes());  

        log.info("Sending update operatorId: {} for emitterId: {} with correlationId: {}",
            operator.getIdentifier(), operator.getEmitterId(), correlationId);

        ProducerRecord<String, OperatorUpdateEvent> producerRecord =
            new ProducerRecord<>(getTopic(accountType), null, operator.getIdentifier().toString(), operatorUpdateEvent, headers);

        kafkaTemplate.send(producerRecord);

    }

    private String getTopic(String accountType) {
        if (AccountType.OPERATOR_HOLDING_ACCOUNT.name().equals(accountType)) {
            return installationSetOperatorRequestTopic;
        }
        if (AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.name().equals(accountType)) {
            return aviationSetOperatorRequestTopic;
        }
        if (AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.name().equals(accountType)) {
            return maritimeSetOperatorRequestTopic;
        }

        throw new UkEtsException("Unexpected Account type.");
    }
}
