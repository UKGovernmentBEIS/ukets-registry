package gov.uk.ets.registry.api.integration.consumer.operator;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.notification.ErrorNotificationProducer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;

@Log4j2
@RequiredArgsConstructor
public abstract class OperatorSetIdOutcomeConsumer {

    private final ErrorNotificationProducer errorNotificationProducer;

    @Transactional
    public void processOutcome(OperatorUpdateEventOutcome outcome, @Headers Map<String, Object> headers) {

        if (outcome.getOutcome() == IntegrationEventOutcome.ERROR) {
            errorNotificationProducer.sendNotifications(outcome.getEvent(), outcome.getErrors(), "Operator ID",
                outcome.getEvent().getOperatorId(), OperationEvent.SET_OPERATOR_ID, headers);
        }
    }
}
