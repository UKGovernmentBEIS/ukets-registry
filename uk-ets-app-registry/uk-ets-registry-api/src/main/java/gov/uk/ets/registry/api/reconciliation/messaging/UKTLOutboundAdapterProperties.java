package gov.uk.ets.registry.api.reconciliation.messaging;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Properties for {@link UKTLOutboundAdapter} component.
 */
@Component
@Getter
public class UKTLOutboundAdapterProperties {
    @Value("${kafka.reconciliation-uktl.question.topic:registry.originating.reconciliation.question.topic}")
    private String reconciliationQuestionTopic;
}
