package gov.uk.ets.registry.api.notification.integration;

import java.util.Date;
import java.util.Set;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class IntegrationDisabledNotification extends EmailNotification {

	private static final long serialVersionUID = 1L;
	private OperationEvent integrationPoint;
    private String correlationId;
    private String payload;
    private Date date;
    private SourceSystem sourceSystem;

    @Builder
    public IntegrationDisabledNotification(Set<String> recipients,
                                               OperationEvent integrationPoint,
                                               String correlationId,
                                               Object event,
                                               Date date,
                                               SourceSystem sourceSystem) {
        super(recipients, GroupNotificationType.INTEGRATION_POINT_DISABLED, "*ACTION REQUIRED* - Integration Point: " + integrationPoint.toString() +" - " + correlationId + " - Integration Point Disabled", null, null);
        this.payload = event.toString();
        this.integrationPoint = integrationPoint;
        this.date = date;
        this.sourceSystem = sourceSystem;
    }
}
