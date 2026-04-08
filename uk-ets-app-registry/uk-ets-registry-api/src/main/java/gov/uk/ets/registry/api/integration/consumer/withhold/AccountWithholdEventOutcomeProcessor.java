package gov.uk.ets.registry.api.integration.consumer.withhold;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import gov.uk.ets.registry.api.notification.AccountWithholdUpdateGroupNotification;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEventOutcome;

import java.util.Map;
import java.util.Set;

@Log4j2
@Component
@RequiredArgsConstructor
public class AccountWithholdEventOutcomeProcessor {

    private final ErrorNotificationProducer errorNotificationProducer;
    private final GroupNotificationClient groupNotificationClient;
    @Value("#{'${mail.integration.notification.address}'.split(';')}")
    private Set<String> integrationNotificationAddresses;

    protected void processOutcome(AccountWithholdUpdateEventOutcome outcome, @Headers Map<String, Object> headers) {

        if (outcome.getOutcome() == IntegrationEventOutcome.ERROR) {
            errorNotificationProducer.sendNotifications(outcome.getEvent(), outcome.getErrors(), "Operator ID",
                    outcome.getEvent().getRegistryId(), OperationEvent.UPDATE_WITHHOLD, headers);
        }
        if (IntegrationEventOutcome.SUCCESS.equals(outcome.getOutcome()) &&
                outcome.getEvent().getWithholdFlag().equals(Boolean.FALSE)) {
            groupNotificationClient.emitGroupNotification(buildNotification(outcome));
        }
    }

    private AccountWithholdUpdateGroupNotification buildNotification(AccountWithholdUpdateEventOutcome outcome) {
        return AccountWithholdUpdateGroupNotification.builder()
                .type(GroupNotificationType.ACCOUNT_WITHHOLD_UPDATE)
                .integrationPoint(OperationEvent.UPDATE_WITHHOLD)
                .registryId(outcome.getEvent().getRegistryId().toString())
                .withholdFlag(outcome.getEvent().getWithholdFlag())
                .year(outcome.getEvent().getReportingYear())
                .recipients(integrationNotificationAddresses)
                .build();
    }
}
