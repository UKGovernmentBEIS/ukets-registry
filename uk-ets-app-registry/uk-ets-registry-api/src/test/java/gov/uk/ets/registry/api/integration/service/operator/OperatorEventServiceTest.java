package gov.uk.ets.registry.api.integration.service.operator;

import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
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
    private OperatorEventService service;

    @BeforeEach
    public void setup() {
        service = new KafkaOperatorEventService(kafkaTemplate);
        ReflectionTestUtils.setField(service, "installationSetOperatorRequestTopic", "installation-topic");
        ReflectionTestUtils.setField(service, "aviationSetOperatorRequestTopic", "aviation-topic");
        ReflectionTestUtils.setField(service, "maritimeSetOperatorRequestTopic", "maritime-topic");
    }

    @Test
    void testProcessEventSuccessful() {
        // given
        MaritimeOperator maritimeOperator = new MaritimeOperator();
        maritimeOperator.setIdentifier(123L);
        maritimeOperator.setEmitterId("emitterId");
        maritimeOperator.setRegulator(RegulatorType.EA);
        String accountType = AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.name();

        // when
        service.updateOperator(maritimeOperator, accountType);

        // then
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(any(ProducerRecord.class));
    }
}
