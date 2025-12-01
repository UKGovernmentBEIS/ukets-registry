package gov.uk.ets.registry.api.integration.notification;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.integration.IntegrationDisabledNotification;
import java.util.Date;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IntegrationPointDisabledNotificationProducer {

    @Value("#{'${mail.integration.notification.address}'.split(';')}")
    private Set<String> registryAdminsAddresses;
    private final GroupNotificationClient groupNotificationClient;

    public void sendNotifications(Object outcome,
            OperationEvent operationEvent,
            String correlationId,
            SourceSystem sourceSystem) {

        registryAdminsAddresses
	    .stream()
	    .map(contactPoint -> buildNotification(outcome, operationEvent, correlationId, sourceSystem))
	    .forEach(groupNotificationClient::emitGroupNotification);
    }
    
    private IntegrationDisabledNotification buildNotification(Object outcome,
            OperationEvent operationEvent,
            String correlationId,
            SourceSystem sourceSystem) {

    return IntegrationDisabledNotification.builder()
			.recipients(registryAdminsAddresses)
			.event(outcome)
			.integrationPoint(operationEvent)
			.sourceSystem(sourceSystem)
			.correlationId(correlationId)
			.date(new Date())
			.build();
    }
}
