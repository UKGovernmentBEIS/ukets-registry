package gov.uk.ets.registry.api.notification.integration;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.netz.integration.model.error.ContactPoint;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class IntegrationErrorOutcomeNotification extends EmailNotification {

    private OperationEvent integrationPoint;
    private String correlationId;
    private ContactPoint contactPoint;
    private Object event;
    private String keyField;
    private Object keyValue;
    private List<IntegrationEventErrorDetails> errors;
    private Date date;
    private SourceSystem sourceSystem;

    @Builder
    public IntegrationErrorOutcomeNotification(Set<String> recipients,
                                               ContactPoint contactPoint,
                                               OperationEvent integrationPoint,
                                               String correlationId,
                                               Object event,
                                               String keyField,
                                               Object keyValue,
                                               List<IntegrationEventErrorDetails> errors,
                                               Date date,
                                               SourceSystem sourceSystem) {
        super(recipients, GroupNotificationType.INTEGRATION_ERROR_OUTCOME, null, null, null);
        this.contactPoint = contactPoint;
        this.integrationPoint = integrationPoint;
        this.correlationId = correlationId;
        this.event = event;
        this.keyField = keyField;
        this.keyValue = keyValue;
        this.errors = errors;
        this.date = date;
        this.sourceSystem = sourceSystem;
    }
}
