package gov.uk.ets.registry.api.integration.service.operator;

import static org.mockito.ArgumentMatchers.any;

import java.util.UUID;

import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.integration.notification.IntegrationPointDisabledNotificationProducer;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;


@ExtendWith(MockitoExtension.class)
class OperatorEventServiceTest {

    @Mock
    private KafkaTemplate<String, OperatorUpdateEvent> kafkaTemplate;
    @Mock
    private IntegrationPointDisabledNotificationProducer integrationPointDisabledNotificationProducer;
    private OperatorEventService service;

    @BeforeEach
    public void setup() {
        service = new KafkaOperatorEventService(integrationPointDisabledNotificationProducer, kafkaTemplate);
        ReflectionTestUtils.setField(service, "installationSetOperatorRequestTopic", "installation-topic");
        ReflectionTestUtils.setField(service, "aviationSetOperatorRequestTopic", "aviation-topic");
        ReflectionTestUtils.setField(service, "maritimeSetOperatorRequestTopic", "maritime-topic");
    }

    @Test
    void testMaritimeProcessEventSuccessful() {
        // given
        ReflectionTestUtils.setField(service, "installationSetOperatorEnabled", false);
        ReflectionTestUtils.setField(service, "aviationSetOperatorEnabled", false);
        ReflectionTestUtils.setField(service, "maritimeSetOperatorEnabled", true);
        MaritimeOperator maritimeOperator = new MaritimeOperator();
        maritimeOperator.setIdentifier(123L);
        maritimeOperator.setEmitterId("emitterId");
        maritimeOperator.setRegulator(RegulatorType.EA);
        String accountType = AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.name();

        // when
        service.updateOperator(maritimeOperator, accountType);

        // then
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(any(ProducerRecord.class));
        Mockito.verify(integrationPointDisabledNotificationProducer, Mockito.times(0)).sendNotifications(any(), any(), any(), any());
    }
    
    @Test
    void testNotifyInstallationIntegrationPointDisabled() {
        // given
        ReflectionTestUtils.setField(service, "installationSetOperatorEnabled", false);
        ReflectionTestUtils.setField(service, "aviationSetOperatorEnabled", false);
        ReflectionTestUtils.setField(service, "maritimeSetOperatorEnabled", false);
        Installation operator = new Installation();
        operator.setIdentifier(123L);
        operator.setEmitterId("emitterId");
        operator.setRegulator(RegulatorType.EA);
        String accountType = AccountType.OPERATOR_HOLDING_ACCOUNT.name();
        String correlationId = UUID.randomUUID().toString();
        // when
        service.updateOperator(operator, accountType, correlationId);

        // then
        OperatorUpdateEvent operatorUpdateEvent = new OperatorUpdateEvent();
        operatorUpdateEvent.setOperatorId(operator.getIdentifier());
        operatorUpdateEvent.setEmitterId(operator.getEmitterId());
        operatorUpdateEvent.setRegulator(operator.getRegulator().name());
        Mockito.verify(integrationPointDisabledNotificationProducer, Mockito.times(1)).sendNotifications(operatorUpdateEvent, OperationEvent.SET_OPERATOR_ID, correlationId ,SourceSystem.METSIA_INSTALLATION);
    }
    
    @Test
    void testNotifyAviationIntegrationPointDisabled() {
        // given
        ReflectionTestUtils.setField(service, "installationSetOperatorEnabled", false);
        ReflectionTestUtils.setField(service, "aviationSetOperatorEnabled", false);
        ReflectionTestUtils.setField(service, "maritimeSetOperatorEnabled", false);
        AircraftOperator operator = new AircraftOperator();
        operator.setIdentifier(123L);
        operator.setEmitterId("emitterId");
        operator.setRegulator(RegulatorType.EA);
        String accountType = AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.name();
        String correlationId = UUID.randomUUID().toString();
        // when
        service.updateOperator(operator, accountType, correlationId);

        // then
        OperatorUpdateEvent operatorUpdateEvent = new OperatorUpdateEvent();
        operatorUpdateEvent.setOperatorId(operator.getIdentifier());
        operatorUpdateEvent.setEmitterId(operator.getEmitterId());
        operatorUpdateEvent.setRegulator(operator.getRegulator().name());
        Mockito.verify(integrationPointDisabledNotificationProducer, Mockito.times(1)).sendNotifications(operatorUpdateEvent, OperationEvent.SET_OPERATOR_ID, correlationId ,SourceSystem.METSIA_AVIATION);
    }
    
    @Test
    void testNotifyMaritimeIntegrationPointDisabled() {
        // given
        ReflectionTestUtils.setField(service, "installationSetOperatorEnabled", false);
        ReflectionTestUtils.setField(service, "aviationSetOperatorEnabled", false);
        ReflectionTestUtils.setField(service, "maritimeSetOperatorEnabled", false);
        MaritimeOperator operator = new MaritimeOperator();
        operator.setIdentifier(123L);
        operator.setEmitterId("emitterId");
        operator.setRegulator(RegulatorType.EA);
        String accountType = AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.name();
        String correlationId = UUID.randomUUID().toString();
        // when
        service.updateOperator(operator, accountType, correlationId);

        // then
        OperatorUpdateEvent operatorUpdateEvent = new OperatorUpdateEvent();
        operatorUpdateEvent.setOperatorId(operator.getIdentifier());
        operatorUpdateEvent.setEmitterId(operator.getEmitterId());
        operatorUpdateEvent.setRegulator(operator.getRegulator().name());
        Mockito.verify(integrationPointDisabledNotificationProducer, Mockito.times(1)).sendNotifications(operatorUpdateEvent, OperationEvent.SET_OPERATOR_ID, correlationId ,SourceSystem.MARITIME);
    }
}
