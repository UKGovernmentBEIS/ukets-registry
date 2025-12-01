package gov.uk.ets.registry.api.integration.notification;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.integration.service.IntegrationHeadersUtil;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.integration.IntegrationErrorOutcomeNotification;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.error.ContactPoint;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

@Component
@RequiredArgsConstructor
public class ErrorNotificationProducer {

    @Value("#{'${mail.integration.notification.address}'.split(';')}")
    private Set<String> registryAdminsAddresses;

    @Value("#{'${mail.integration.desnz.notification.address}'.split(';')}")
    private Set<String> desnzAddresses;

    @Value("#{'${mail.integration.service-desk.notification.address}'.split(';')}")
    private Set<String> serviceDeskAddresses;

    @Value("#{'${mail.integration.tu-suppor.notification.address}'.split(';')}")
    private Set<String> tuSupportAddresses;

    private final GroupNotificationClient groupNotificationClient;
    private final IntegrationHeadersUtil util;

    private final List<OperationEvent> registryOnlyOperations = List.of(OperationEvent.SET_OPERATOR_ID);
    private Map<ContactPoint, Set<String>> addressesPerContactPoint;

    @PostConstruct
    public void init() {
        addressesPerContactPoint = new EnumMap<>(ContactPoint.class);
        addressesPerContactPoint.put(ContactPoint.REGISTRY_ADMINISTRATORS, registryAdminsAddresses);
        addressesPerContactPoint.put(ContactPoint.DESNZ, desnzAddresses);
        addressesPerContactPoint.put(ContactPoint.SERVICE_DESK, serviceDeskAddresses);
        addressesPerContactPoint.put(ContactPoint.TU_SUPPORT, tuSupportAddresses);
    }

    public <T> void sendNotifications(T outcome,
                                      List<IntegrationEventErrorDetails> errorDetails,
                                      String keyField,
                                      Object keyValue,
                                      OperationEvent operationEvent,
                                      Map<String, Object> headers) {

        Predicate<ContactPoint> operationRelated = contactPoint -> !registryOnlyOperations.contains(operationEvent)
            || contactPoint == ContactPoint.REGISTRY_ADMINISTRATORS;

        Predicate<ContactPoint> errorRelated =
            contactPoint -> errorDetails.stream()
                .map(IntegrationEventErrorDetails::getError)
                .anyMatch(error -> error.isRelatedFor(contactPoint));

        addressesPerContactPoint.keySet()
            .stream()
            .filter(operationRelated)
            .filter(errorRelated)
            .map(contactPoint ->
                buildNotification(outcome, errorDetails, keyField, keyValue, operationEvent, headers, contactPoint))
            .forEach(groupNotificationClient::emitGroupNotification);
    }

    private <T> IntegrationErrorOutcomeNotification buildNotification(T outcome,
                                                                      List<IntegrationEventErrorDetails> errorDetails,
                                                                      String keyField,
                                                                      Object keyValue,
                                                                      OperationEvent operationEvent,
                                                                      Map<String, Object> headers,
                                                                      ContactPoint contactPoint) {

        Set<String> recipients = addressesPerContactPoint.get(contactPoint);
        String correlationId = util.getCorrelationId(headers);
        SourceSystem sourceSystem = util.getSourceSystem(headers);

        return IntegrationErrorOutcomeNotification.builder()
            .recipients(recipients)
            .contactPoint(contactPoint)
            .event(outcome)
            .keyField(keyField)
            .keyValue(keyValue)
            .integrationPoint(operationEvent)
            .sourceSystem(sourceSystem)
            .correlationId(correlationId)
            .date(new Date())
            .errors(errorDetails)
            .build();
    }
}
