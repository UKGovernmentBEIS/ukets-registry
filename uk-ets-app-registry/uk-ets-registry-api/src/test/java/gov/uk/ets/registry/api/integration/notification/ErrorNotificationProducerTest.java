package gov.uk.ets.registry.api.integration.notification;

import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.error.IntegrationEventError;
import gov.uk.ets.registry.api.integration.error.IntegrationEventErrorDetails;
import gov.uk.ets.registry.api.integration.service.IntegrationHeadersUtil;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ErrorNotificationProducerTest {

    @Mock
    private GroupNotificationClient groupNotificationClient;
    @Mock
    private IntegrationHeadersUtil util;

    private ErrorNotificationProducer notificationProducer;

    @BeforeEach
    public void setup() {
        notificationProducer = new ErrorNotificationProducer(groupNotificationClient, util);
        ReflectionTestUtils.setField(notificationProducer, "registryAdminsAddresses", Set.of("registry@domain.com"));
        ReflectionTestUtils.setField(notificationProducer, "desnzAddresses", Set.of("desnz@domain.com"));
        ReflectionTestUtils.setField(notificationProducer, "serviceDeskAddresses", Set.of("service.desk@domain.com"));
        ReflectionTestUtils.setField(notificationProducer, "tuSupportAddresses", Set.of("support@domain.com"));
        notificationProducer.init();
    }

    @Test
    void testSendEmissionsNotifications() {
        // given
        Object outcome = new Object();
        List<IntegrationEventErrorDetails> errorDetails =
            List.of(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0108, "test"));
        String keyField = "key";
        Object keyValue = "value";
        OperationEvent operationEvent = OperationEvent.UPDATE_EMISSIONS_VALUE;
        Map<String, Object> headers = Map.of();

        // when
        notificationProducer.sendNotifications(outcome, errorDetails, keyField, keyValue, operationEvent, headers);

        // then
        Mockito.verify(util, Mockito.times(4)).getCorrelationId(headers);
        Mockito.verify(util, Mockito.times(4)).getSourceSystem(headers);
        Mockito.verify(groupNotificationClient, Mockito.times(4))
            .emitGroupNotification(any(GroupNotification.class));
    }

    @Test
    void testSendOperatorNotification() {
        // given
        Object outcome = new Object();
        List<IntegrationEventErrorDetails> errorDetails =
            List.of(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0200, "test"));
        String keyField = "key";
        Object keyValue = "value";
        OperationEvent operationEvent = OperationEvent.SET_OPERATOR_ID;
        Map<String, Object> headers = Map.of();

        // when
        notificationProducer.sendNotifications(outcome, errorDetails, keyField, keyValue, operationEvent, headers);

        // then
        Mockito.verify(util, Mockito.times(1)).getCorrelationId(headers);
        Mockito.verify(util, Mockito.times(1)).getSourceSystem(headers);
        Mockito.verify(groupNotificationClient, Mockito.times(1))
            .emitGroupNotification(any(GroupNotification.class));
    }
}
