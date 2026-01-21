package gov.uk.ets.registry.api.integration.consumer.exemption;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEventOutcome;

import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class AccountExemptionEventOutcomeProcessor {

    private final ErrorNotificationProducer errorNotificationProducer;

    protected void processOutcome(AccountExemptionUpdateEventOutcome outcome, @Headers Map<String, Object> headers) {

        if (outcome.getOutcome() == IntegrationEventOutcome.ERROR) {
            errorNotificationProducer.sendNotifications(outcome.getEvent(), outcome.getErrors(), "Operator ID",
                    outcome.getEvent().getRegistryId(), OperationEvent.UPDATE_EXEMPTION, headers);
        }
    }
}
