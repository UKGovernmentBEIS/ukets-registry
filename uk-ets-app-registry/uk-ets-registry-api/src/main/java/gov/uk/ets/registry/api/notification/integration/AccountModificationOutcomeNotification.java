package gov.uk.ets.registry.api.notification.integration;

import gov.uk.ets.registry.api.common.DateUtils;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public abstract class AccountModificationOutcomeNotification extends EmailNotification {

    private String integrationPoint;
    private String correlationId;
    private String accountFullIdentifier;
    private String emitterId;
    private String accountName;
    private String accountType;
    private Date date;
    private SourceSystem sourceSystem;

    protected AccountModificationOutcomeNotification(Set<String> recipients,
                                                     GroupNotificationType type,
                                                     String integrationPoint,
                                                     String correlationId,
                                                     String accountFullIdentifier,
                                                     String emitterId,
                                                     String accountName,
                                                     String accountType,
                                                     Date date,
                                                     SourceSystem sourceSystem) {
        super(recipients, type, null, null, null);
        this.integrationPoint = integrationPoint;
        this.correlationId = correlationId;
        this.accountFullIdentifier = accountFullIdentifier;
        this.emitterId = emitterId;
        this.accountName = accountName;
        this.accountType = accountType;
        this.date = date;
        this.sourceSystem = sourceSystem;
    }

    public Map<String, Object> getMapParameters() {
        final Map<String, Object> params = new HashMap<>();
        params.put("type", getType().name());
        params.put("integrationPoint", integrationPoint);
        params.put("correlationId", correlationId);

        params.put("accountFullIdentifier", accountFullIdentifier);
        params.put("emitterId", emitterId);
        params.put("accountName", accountName);
        params.put("accountType", accountType);

        params.put("date", DateUtils.prettyCalendarDate(date));
        params.put("time", DateUtils.formatLondonZonedTime(date));
        params.put("sourceSystem", sourceSystem.name());
        return params;
    }
}
